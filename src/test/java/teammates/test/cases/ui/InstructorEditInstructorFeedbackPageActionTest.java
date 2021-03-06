package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorEditInstructorFeedbackPageAction;
import teammates.ui.controller.ShowPageResult;

public class InstructorEditInstructorFeedbackPageActionTest extends BaseActionTest {

    private static DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        dataBundle = loadDataBundle("/InstructorEditInstructorFeedbackPageTest.json");
        removeAndRestoreDatastoreFromJson("/InstructorEditInstructorFeedbackPageTest.json");
        
        uri = Const.ActionURIs.INSTRUCTOR_EDIT_INSTRUCTOR_FEEDBACK_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor = dataBundle.instructors.get("IEIFPTCourseinstr");
        InstructorAttributes moderatedInstructor = dataBundle.instructors.get("IEIFPTCoursehelper1");
        InstructorEditInstructorFeedbackPageAction editInstructorFpAction;
        ShowPageResult showPageResult;
        
        String courseId = moderatedInstructor.courseId;
        String feedbackSessionName = "";
        String moderatedInstructorEmail = "IEIFPTCoursehelper1@gmail.tmt";
        String[] submissionParams;

        gaeSimulation.loginAsInstructor(instructor.googleId);

        ______TS("typical success case");
        feedbackSessionName = "First feedback session";
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedInstructorEmail
        };
        
        editInstructorFpAction = getAction(submissionParams);
        showPageResult = (ShowPageResult) editInstructorFpAction.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT + "?error=false&user=" + instructor.googleId,
                     showPageResult.getDestinationWithParams());
        assertEquals("", showPageResult.getStatusMessage());
        AssertHelper.assertLogMessageEquals("TEAMMATESLOG|||instructorEditInstructorFeedbackPage|||instructorEditInstructorFeedbackPage"
                + "|||true|||Instructor|||IEIFPTCourseinstr|||IEIFPTCourseinstr|||IEIFPTCourseintr@course1.tmt|||"
                + "Moderating feedback session for instructor (" + moderatedInstructor.email + ")<br>"
                + "Session Name: First feedback session<br>Course ID: IEIFPTCourse|||"
                + "/page/instructorEditInstructorFeedbackPage",
                editInstructorFpAction.getLogMessage());
        
        ______TS("success: another feedback");
        feedbackSessionName = "Another feedback session";
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedInstructorEmail
        };
        
        editInstructorFpAction = getAction(submissionParams);
        showPageResult = (ShowPageResult) editInstructorFpAction.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT + "?error=false&user=" + instructor.googleId,
                     showPageResult.getDestinationWithParams());
        assertEquals("", showPageResult.getStatusMessage());
        AssertHelper.assertLogMessageEquals("TEAMMATESLOG|||instructorEditInstructorFeedbackPage|||instructorEditInstructorFeedbackPage"
                + "|||true|||Instructor|||IEIFPTCourseinstr|||IEIFPTCourseinstr|||IEIFPTCourseintr@course1.tmt|||"
                + "Moderating feedback session for instructor (" + moderatedInstructor.email + ")<br>"
                + "Session Name: Another feedback session<br>Course ID: IEIFPTCourse|||"
                + "/page/instructorEditInstructorFeedbackPage",
                editInstructorFpAction.getLogMessage());
        
        ______TS("failure: does not have privilege (helper can't moderate instructor)");
        gaeSimulation.loginAsInstructor(moderatedInstructor.googleId);
        feedbackSessionName = "First feedback session";
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedInstructorEmail
        };
        
        try {
            editInstructorFpAction = getAction(submissionParams);
            editInstructorFpAction.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] is not accessible "
                         + "to instructor [" + moderatedInstructor.email + "] "
                         + "for privilege [canmodifysession]", e.getMessage());
        }

        ______TS("failure: accessing non-existent moderatedinstructor email");
        gaeSimulation.loginAsInstructor(instructor.googleId);
        moderatedInstructorEmail = "non-exIstentEmail@gsail.tmt";
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedInstructorEmail
        };
        
        try {
            editInstructorFpAction = getAction(submissionParams);
            editInstructorFpAction.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (EntityNotFoundException enfe) {
            assertEquals("Instructor Email " + moderatedInstructorEmail
                         + " does not exist in " + courseId + ".", enfe.getMessage());
        }
    }

    private InstructorEditInstructorFeedbackPageAction getAction(String... params) {
        return (InstructorEditInstructorFeedbackPageAction) (gaeSimulation.getActionObject(uri, params));
    }
}
