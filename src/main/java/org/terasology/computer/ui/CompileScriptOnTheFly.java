// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.ui;

import com.gempukku.lang.IllegalSyntaxException;
import com.gempukku.lang.parser.ScriptParser;
import com.gempukku.lang.parser.ScriptParsingCallback;
import org.terasology.computer.system.common.ComputerLanguageContext;
import org.terasology.computer.system.common.ComputerLanguageContextInitializer;
import org.terasology.computer.system.common.DocumentedObjectDefinition;
import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.engine.rendering.nui.widgets.browser.data.ParagraphData;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CompileScriptOnTheFly {
    private final Object lockObject = new Object();
    private final ScriptParser scriptParser = new ScriptParser();
    private final Set<String> predefinedVariables = new HashSet<>();
    private volatile CompileStatus compileStatus;
    private volatile String scriptText;
    private volatile boolean finishedEditing;

    public CompileScriptOnTheFly(ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addObject(String object, DocumentedObjectDefinition objectDefinition,
                                          String objectDescription,
                                          Collection<ParagraphData> additionalParagraphs) {
                        predefinedVariables.add(object);
                    }

                    @Override
                    public void addObjectType(String objectType, Collection<ParagraphData> documentation) {
                        // Ignore
                    }

                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description,
                                                  Collection<ParagraphData> additionalParagraphs) {
                        // Ignore for now
                    }
                }
        );
        predefinedVariables.add("args");
    }

    public void startCompiler() {
        finishedEditing = false;
        Thread thr = new Thread(
                new Runnable() {
                    public void run() {
                        keepCompilingUntilFinishedEditing();
                    }
                });
        thr.setDaemon(true);
        thr.start();
    }

    public void submitCompileRequest(String newScriptText) {
        synchronized (lockObject) {
            scriptText = newScriptText;
            compileStatus = null;
            lockObject.notifyAll();
        }
    }

    public CompileStatus getCompileStatus() {
        return compileStatus;
    }

    public void finishedEditing() {
        finishedEditing = true;
        synchronized (lockObject) {
            lockObject.notifyAll();
        }
    }

    private void keepCompilingUntilFinishedEditing() {
        while (!finishedEditing) {
            String scriptTextToCompile;
            synchronized (lockObject) {
                scriptTextToCompile = scriptText;
                this.scriptText = null;
            }

            CompileStatus newCompileStatus = null;
            if (scriptTextToCompile != null) {
                ParseInfoProducer parseInfoProducer = new ParseInfoProducer();
                try {
                    scriptParser.parseScript(new StringReader(scriptTextToCompile), predefinedVariables,
                            parseInfoProducer);
                    newCompileStatus = new CompileStatus(true, null, parseInfoProducer.result);
                } catch (IllegalSyntaxException exp) {
                    newCompileStatus = new CompileStatus(false, exp, parseInfoProducer.result);
                } catch (IOException exp) {
                    // Can't really happen, as we use StringReader, but oh well
                    newCompileStatus = new CompileStatus(false, null, parseInfoProducer.result);
                } catch (RuntimeException exp) {
                    newCompileStatus = new CompileStatus(false, null, parseInfoProducer.result);
                }
            }

            synchronized (lockObject) {
                // If new request was not created in the meantime, wait for notify
                if (this.scriptText == null) {
                    compileStatus = newCompileStatus;
                    try {
                        lockObject.wait();
                    } catch (InterruptedException exp) {
                        // Ignore, CheckStyle made me add one statement
                        exp.printStackTrace();
                    }
                }
            }
        }
    }

    public static final class CompileStatus {
        public final boolean success;
        public final IllegalSyntaxException error;
        public final Collection<ParseInfo> parseInfo;

        private CompileStatus(boolean success, IllegalSyntaxException error, Collection<ParseInfo> parseInfo) {
            this.error = error;
            this.success = success;
            this.parseInfo = Collections.unmodifiableCollection(parseInfo);
        }
    }

    public static class ParseInfo {
        public final int line;
        public final int column;
        public final int length;
        public final ScriptParsingCallback.Type type;

        public ParseInfo(int line, int column, int length, ScriptParsingCallback.Type type) {
            this.line = line;
            this.column = column;
            this.length = length;
            this.type = type;
        }
    }

    private static class ParseInfoProducer implements ScriptParsingCallback {
        private final List<ParseInfo> result = new LinkedList<>();

        @Override
        public void parsed(int line, int column, int length, Type type) {
            result.add(new ParseInfo(line, column, length, type));
        }
    }
}
