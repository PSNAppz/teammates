package teammates.ui.template;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackQuestionAttributes;

public class InstructorFeedbackResultsQuestionTable {

    private String courseId;
    private String feedbackSessionName;
    
    private String panelClass;
    private String ajaxClass;
    
    private List<InstructorFeedbackResultsResponseRow> responses;
    
    // store the attributes of the question for the non-display purposes 
    // such as form inputs
    private FeedbackQuestionAttributes question;

    private String questionText;
    private String additionalInfoText;
    private String questionStatisticsTable;
    
    private boolean isHasResponses;
    private boolean isShowResponseRows;
    
    private boolean isCollapsible;
    private boolean isBoldQuestionNumber;

    private List<ElementTag> columns;
    private Map<String, Boolean> isColumnSortable;

    public InstructorFeedbackResultsQuestionTable(
                                          final boolean isHasResponses,
                                          final String questionStatisticsHtml,
                                          final List<InstructorFeedbackResultsResponseRow> responseRows,
                                          final FeedbackQuestionAttributes question,
                                          final String questionText,
                                          final String additionalInfoText,
                                          final List<ElementTag> columns,
                                          final Map<String, Boolean> isColumnSortable) {
        this.courseId = question.courseId;
        this.feedbackSessionName = question.feedbackSessionName;
        
        this.questionStatisticsTable = questionStatisticsHtml;
        this.responses = responseRows;
        
        this.isHasResponses = isHasResponses;  
        
        this.question = question;
        
        this.questionText = questionText;
        
        this.panelClass = "panel-info";
        
        this.additionalInfoText = additionalInfoText;
        
        this.columns = columns;
        
        this.isBoldQuestionNumber = true;
        this.isColumnSortable = isColumnSortable;
    }

    public List<InstructorFeedbackResultsResponseRow> getResponses() {
        return responses;
    }

    public String getPanelClass() {
        return panelClass;
    }

    public FeedbackQuestionAttributes getQuestion() {
        return question;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getAdditionalInfoText() {
        return additionalInfoText;
    }

    public String getQuestionStatisticsTable() {
        return questionStatisticsTable;
    }

    public boolean isHasResponses() {
        return isHasResponses;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public boolean isShowResponseRows() {
        return isShowResponseRows;
    }

    public List<ElementTag> getColumns() {
        return columns;
    }

    public boolean isCollapsible() {
        return isCollapsible;
    }
    
    public boolean isBoldQuestionNumber() {
        return isBoldQuestionNumber;
    }
    
    public Map<String, Boolean> getIsColumnSortable() {
        return isColumnSortable;
    }

    public void setShowResponseRows(final boolean isShowResponseRows) {
        this.isShowResponseRows = isShowResponseRows;
    }

    public void setCollapsible(final boolean isCollapsible) {
        this.isCollapsible = isCollapsible;
    }

    public void setBoldQuestionNumber(final boolean isBoldQuestionNumber) {
        this.isBoldQuestionNumber = isBoldQuestionNumber;
    }

    public void setAjaxClass(final String ajaxClass) {
        this.ajaxClass = ajaxClass;
    }

    public String getAjaxClass() {
        return ajaxClass;
    }

    public void setHasResponses(final boolean isHasResponses) {
        this.isHasResponses = isHasResponses;
    }

    public static void sortByQuestionNumber(final List<InstructorFeedbackResultsQuestionTable> questionTables) {
        Collections.sort(questionTables, new Comparator<InstructorFeedbackResultsQuestionTable>() {
            public int compare(final InstructorFeedbackResultsQuestionTable questionTable1, final InstructorFeedbackResultsQuestionTable questionTable2) {
                return questionTable1.question.questionNumber - questionTable2.question.questionNumber;
            }
        });
    }
    
}
