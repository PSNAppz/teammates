package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseStudentDetailsEditPage;

/**
 * Covers the 'edit student details' functionality for instructors.
 * SUT: {@link InstructorCourseStudentDetailsEditPage}.
 */
public class InstructorCourseStudentDetailsEditPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static InstructorCourseStudentDetailsEditPage editPage;
    private static DataBundle testData;

    @BeforeClass
    public static void classSetup() {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorCourseStudentDetailsEditPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }

    @Test
    public void testAll() throws Exception {
        testContent();
        testInputValidation();
//        no links to check
        testEditAction();
    }
    
    public void testContent() throws Exception {
        
        String instructorId = testData.instructors.get("CCSDEditUiT.instr").googleId;
        String courseId = testData.courses.get("CCSDEditUiT.CS2104").getId();
        
        ______TS("content: unregistered student");
        
        AppUrl editPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT)
                                        .withUserId(instructorId)
                                        .withCourseId(courseId)
                                        .withStudentEmail(testData.students.get("unregisteredStudent").email);
        
        editPage = loginAdminToPage(browser, editPageUrl, InstructorCourseStudentDetailsEditPage.class);
        editPage.verifyHtmlMainContent("/InstructorCourseStudentEditUnregisteredPage.html");
        
        ______TS("content: registered student");
        
        editPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT)
            .withUserId(instructorId)
            .withCourseId(courseId)
            .withStudentEmail(testData.students.get("registeredStudent").email);
        
        editPage = loginAdminToPage(browser, editPageUrl, InstructorCourseStudentDetailsEditPage.class);

        // This is the full HTML verification for Instructor Course Student Edit Page, the rest can all be verifyMainHtml
        editPage.verifyHtml("/InstructorCourseStudentEditPage.html");
    }
    
    public void testInputValidation() {
        
        ______TS("input validation");
        
        String teamNameFieldName = "a team name";
        String personNameFieldName = "a person name";
        
        editPage.submitUnsuccessfully(null, "", null, null)
                .verifyStatus(String.format(FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE,
                    "", teamNameFieldName, FieldValidator.REASON_EMPTY, teamNameFieldName, FieldValidator.TEAM_NAME_MAX_LENGTH));
        
        ______TS("empty student name and the team field is edited");
        String newTeamName = "New teamname";
        editPage.submitUnsuccessfully("", newTeamName, null, null)
                .verifyStatus(String.format(FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE,
                                            "", personNameFieldName, FieldValidator.REASON_EMPTY, personNameFieldName,
                                            FieldValidator.PERSON_NAME_MAX_LENGTH));
        
        ______TS("long student name and the team field is not edited");
        String invalidStudentName = StringHelper.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);
        editPage.submitUnsuccessfully(invalidStudentName, null, null, null)
                .verifyStatus(String.format(FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE,
                                            invalidStudentName, personNameFieldName, FieldValidator.REASON_TOO_LONG,
                                            personNameFieldName, FieldValidator.PERSON_NAME_MAX_LENGTH));
        
        String newStudentName = "New guy";
        String invalidTeamName = StringHelper.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH + 1);
        editPage.submitUnsuccessfully(newStudentName, invalidTeamName, null, null)
                .verifyStatus(String.format(FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE,
                                            invalidTeamName, teamNameFieldName, FieldValidator.REASON_TOO_LONG,
                                            teamNameFieldName, FieldValidator.TEAM_NAME_MAX_LENGTH));
        
        String invalidEmail = "invalidemail";
        editPage.submitUnsuccessfully(newStudentName, newTeamName, invalidEmail, null)
                .verifyStatus(String.format(FieldValidator.EMAIL_ERROR_MESSAGE, invalidEmail,
                                            FieldValidator.REASON_INCORRECT_FORMAT));
    }

    public void testEditAction() {
        
        ______TS("Error case, invalid email parameter (email already taken by others)");

        StudentAttributes anotherStudent = testData.students.get("unregisteredStudent");

        editPage = editPage.submitUnsuccessfully("New name2", "New team2", anotherStudent.email, "New comments2");
        editPage.verifyStatus(String.format(FieldValidator.EMAIL_TAKEN_MESSAGE, anotherStudent.name,
                                            anotherStudent.email));
        editPage.verifyIsCorrectPage("CCSDEditUiT.jose.tmms@gmail.tmt");
            
        // Verify data
        StudentAttributes student = BackDoor.getStudent(testData.courses.get("CCSDEditUiT.CS2104").getId(),
                                                                             "CCSDEditUiT.jose.tmms@gmail.tmt");
        assertEquals("José Gómez</option></td></div>'\"", student.name);
        assertEquals("Team 1</td></div>'\"", student.team);
        assertEquals(testData.students.get("registeredStudent").googleId, student.googleId);
        assertEquals("CCSDEditUiT.jose.tmms@gmail.tmt", student.email);
        assertEquals("This student's name is José Gómez</option></td></div>'\"", student.comments);

        ______TS("edit action");
        
        InstructorCourseDetailsPage detailsPage = editPage.submitSuccessfully("New name", "New team", "newemail@gmail.tmt", "New comments");
        detailsPage.verifyStatus(Const.StatusMessages.STUDENT_EDITED);
        detailsPage.verifyIsCorrectPage(testData.courses.get("CCSDEditUiT.CS2104").getId());
            
        // Verify data
        student = BackDoor.getStudent(testData.courses.get("CCSDEditUiT.CS2104").getId(),
                                      "newemail@gmail.tmt");
        assertEquals("New name", student.name);
        assertEquals("New team", student.team);
        assertEquals(testData.students.get("registeredStudent").googleId, student.googleId);
        assertEquals("newemail@gmail.tmt", student.email);
        assertEquals("New comments", student.comments);
    }

    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }
}
