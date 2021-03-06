package teammates.ui.controller;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.UserType;
import teammates.common.util.Const;
import teammates.logic.api.Logic;

@SuppressWarnings("serial")
/**
 * Servlet to handle Login
 */
public class LoginServlet extends HttpServlet {
    
    @Override
    public final void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.doPost(req, resp);
    }

    @Override
    public final void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Logic server = new Logic();
        UserType user = server.getCurrentUser();
        boolean isInstructor = req.getParameter(Const.ParamsNames.LOGIN_INSTRUCTOR) != null;
        boolean isStudent = req.getParameter(Const.ParamsNames.LOGIN_STUDENT) != null;
        boolean isAdmin = req.getParameter(Const.ParamsNames.LOGIN_ADMIN) != null;
        
        if (isInstructor) {
            if (isMasqueradeMode(user)) {
                resp.sendRedirect(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
            } else {
                resp.sendRedirect(Logic.getLoginUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE));
            }
        } else if (isStudent) {
            if (isMasqueradeMode(user)) {
                resp.sendRedirect(Const.ActionURIs.STUDENT_HOME_PAGE);
            } else {
                resp.sendRedirect(Logic.getLoginUrl(Const.ActionURIs.STUDENT_HOME_PAGE));
            }
        //TODO: do we need this branch?
        } else if (isAdmin) {
            if (isMasqueradeMode(user)) {
                resp.sendRedirect(Const.ActionURIs.ADMIN_HOME_PAGE);
            } else {
                resp.sendRedirect(Logic.getLoginUrl(Const.ActionURIs.ADMIN_HOME_PAGE));
            }
        } else {
            resp.sendRedirect(Const.ViewURIs.ERROR_PAGE);
        }
    }

    private boolean isMasqueradeMode(UserType user) {
        return user != null;
    }
}
