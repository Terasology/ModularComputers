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
import org.terasology.browser.data.ParagraphData;
import org.terasology.computer.system.common.ComputerLanguageContext;
import org.terasology.computer.system.common.ComputerLanguageContextInitializer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
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
                    public void addObject(String object, ObjectDefinition objectDefinition, Collection<ParagraphData> objectDescription, Map<String, Collection<ParagraphData>> functionDescriptions, Map<String, Map<String, Collection<ParagraphData>>> functionParametersDescriptions, Map<String, Collection<ParagraphData>> functionReturnDescriptions) {
                        _predefinedVariables.add(object);
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
                try {
                    _scriptParser.parseScript(new StringReader(scriptText), _predefinedVariables);
                    newCompileStatus = new CompileStatus(true, null);
                } catch (IllegalSyntaxException exp) {
                    newCompileStatus = new CompileStatus(false, exp);
                } catch (IOException exp) {
                    // Can't really happen, as we use StringReader, but oh well
                    newCompileStatus = new CompileStatus(false, null);
                } catch (RuntimeException exp) {
                    newCompileStatus = new CompileStatus(false, null);
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

    public class CompileStatus {
        public final boolean success;
        public final IllegalSyntaxException error;

        public CompileStatus(boolean success, IllegalSyntaxException error) {
            this.error = error;
            this.success = success;
        }
    }
}