/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.computer.ui;

import com.gempukku.lang.IllegalSyntaxException;
import com.gempukku.lang.ObjectDefinition;
import com.gempukku.lang.parser.ScriptParser;
import com.gempukku.lang.parser.ScriptParsingCallback;
import org.terasology.browser.data.ParagraphData;
import org.terasology.computer.system.common.ComputerLanguageContext;
import org.terasology.computer.system.common.ComputerLanguageContextInitializer;
import org.terasology.computer.system.server.lang.ComputerModule;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompileScriptOnTheFly {
    private volatile CompileStatus _compileStatus;
    private volatile String _scriptText;
    private volatile boolean _finishedEditing;

    private final Object _lockObject = new Object();
    private ScriptParser _scriptParser = new ScriptParser();
    private Set<String> _predefinedVariables = new HashSet<String>();

    public CompileScriptOnTheFly(ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addObject(String object, ObjectDefinition objectDefinition, String objectDescription, Collection<ParagraphData> additionalParagraphs, Map<String, String> functionDescriptions, Map<String, Map<String, String>> functionParametersDescriptions, Map<String, String> functionReturnDescriptions, Map<String, Collection<ParagraphData>> functionAdditionalParagraphs) {
                        _predefinedVariables.add(object);
                    }

                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description, Collection<ParagraphData> additionalParagraphs, Map<String, String> methodDescriptions, Map<String, Map<String, String>> methodParametersDescriptions, Map<String, String> methodReturnDescriptions, Map<String, Collection<ParagraphData>> methodAdditionalParagraphs) {
                        // Ignore for now
                    }
                }
        );
    }

    public void startCompiler() {
        _finishedEditing = false;
        Thread thr = new Thread(
                new Runnable() {
                    public void run() {
                        keepCompilingUntilFinishedEditing();
                    }
                });
        thr.setDaemon(true);
        thr.start();
    }

    public void submitCompileRequest(String scriptText) {
        synchronized (_lockObject) {
            _scriptText = scriptText;
            _compileStatus = null;
            _lockObject.notifyAll();
        }
    }

    public CompileStatus getCompileStatus() {
        return _compileStatus;
    }

    public void finishedEditing() {
        _finishedEditing = true;
        synchronized (_lockObject) {
            _lockObject.notifyAll();
        }
    }

    private void keepCompilingUntilFinishedEditing() {
        while (!_finishedEditing) {
            String scriptText = null;
            synchronized (_lockObject) {
                scriptText = _scriptText;
                _scriptText = null;
            }

            CompileStatus newCompileStatus = null;
            if (scriptText != null) {
                ParseInfoProducer parseInfoProducer = new ParseInfoProducer();
                try {
                    _scriptParser.parseScript(new StringReader(scriptText), _predefinedVariables, parseInfoProducer);
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

            synchronized (_lockObject) {
                // If new request was not created in the meantime, wait for notify
                if (_scriptText == null) {
                    _compileStatus = newCompileStatus;
                    try {
                        _lockObject.wait();
                    } catch (InterruptedException exp) {

                    }
                }
            }
        }
    }

    public static class CompileStatus {
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
        private List<ParseInfo> result = new LinkedList<>();

        @Override
        public void parsed(int line, int column, int length, Type type) {
            result.add(new ParseInfo(line, column, length, type));
        }
    }
}