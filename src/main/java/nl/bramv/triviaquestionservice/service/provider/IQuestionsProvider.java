package nl.bramv.triviaquestionservice.service.provider;

import nl.bramv.triviaquestionservice.model.Question;
import nl.bramv.triviaquestionservice.model.QuestionsRequest;

import java.util.List;

/// Contract for every implementation:
///   - Return at most `request.amount()` questions.
///   - Never return null or an empty list. No matching questions
///     must be reported using [QuestionsProviderNoResultsException].
///   - Return questions with all required fields populated.
///   - Treat null category, difficulty and type filters as no preference.
///   - Throw only [QuestionsProviderException] or one of its subclasses.
public interface IQuestionsProvider {
    List<Question> getQuestions(QuestionsRequest request
    );
}
