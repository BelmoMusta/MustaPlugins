package musta.belmo.plugins.ast;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class RestWsCompletionContributor extends CompletionContributor implements DumbAware {

    public RestWsCompletionContributor() {
        this.extend(CompletionType.BASIC, PlatformPatterns.psiElement(),
                new CompletionProvider<>() {
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        Editor editor = parameters.getEditor();
                        CaretModel caretModel = editor.getCaretModel();
                        CharSequence documentText =
                                editor.getDocument().getCharsSequence().subSequence(caretModel.getVisualLineStart(),
                                        caretModel.getVisualLineEnd());

                        String trimmedSelectedFragment = documentText.toString().trim();
                        if (trimmedSelectedFragment.toUpperCase().matches("^(GET|POST|DELETE|PUT|PATCH)\\s+\\S+$")) {

                            result.stopHere();
                        } else {
                            return; // to pass to other contributors
                        }

                        WsSignature wsSignature = new WsSignature(trimmedSelectedFragment);
                        if (!wsSignature.isValid()) {
                            return;
                        }
                        result.addElement(LookupElementBuilder.create("Add controller")
                                .bold()
                                .withInsertHandler((insertionContext, lookupElement) -> {
                                    insertionContext.getEditor().getDocument().deleteString(caretModel.getVisualLineStart(), caretModel.getVisualLineEnd());
                                    RestWSCreatorTransformer restWSCreatorTransformer =
                                            new RestWSCreatorTransformer(wsSignature,
                                                    caretModel.getOffset());
                                    PsiDocumentManager documentManager =
                                            PsiDocumentManager.getInstance(insertionContext.getProject());
                                    documentManager.commitDocument(insertionContext.getDocument());
                                    restWSCreatorTransformer.transformPsi(insertionContext.getFile());
                                }));
                    }
                });
    }

}