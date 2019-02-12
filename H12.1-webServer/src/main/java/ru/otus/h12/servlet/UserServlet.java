package ru.otus.h12.servlet;

import ru.otus.h12.dataset.UserDataSet;
import ru.otus.h12.database.DBService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class UserServlet extends HttpServlet {

    private static final String USER_PAGE_TEMPLATE = "user.html";

    private static final String SUBMIT_ADD_PARAMETER_NAME = "submit_add";
    private static final String SUBMIT_SEARCH_PARAMETER_NAME = "submit_search";
    private static final String ID_PARAMETER_NAME = "id";
    private static final String NAME_PARAMETER_NAME = "name";
    private static final String AGE_PARAMETER_NAME = "age";

    private static final String USER_NAME_VARIABLE_NAME = "userName";
    private static final String USER_COUNT_VARIABLE_NAME = "userCount";
    private static final String IS_ADD_ERROR_VARIABLE_NAME = "isAddError";
    private static final String IS_SEARCH_ERROR_VARIABLE_NAME = "isSearchError";

    private static final String USER_FIELDS_ERROR = "Please fill the form";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String USER_NO_ID_SET = "No user ID specified";

    private static final String CONTENT_TYPE = "text/html;charset=utf-8";

    private final TemplateProcessor templateProcessor;
    private final DBService dbService;

    private Long cachedUserCount = null;

    @SuppressWarnings("WeakerAccess")
    public UserServlet(TemplateProcessor templateProcessor, DBService dbService) throws IOException {
        this.templateProcessor = templateProcessor;
        this.dbService = dbService;
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> pageVariables = PageVariables.create(request);

        if (null != request.getParameter(SUBMIT_ADD_PARAMETER_NAME)) {

            String userName = request.getParameter(NAME_PARAMETER_NAME);
            String userAge = request.getParameter(AGE_PARAMETER_NAME);

            if (!userName.equals("") && !userAge.equals("")) {

                UserDataSet user = new UserDataSet(userName, Integer.parseInt(userAge));

                try {

                    dbService.save(user);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                pageVariables.put(IS_ADD_ERROR_VARIABLE_NAME, USER_FIELDS_ERROR);
            }
        }

        cachedUserCount = dbService.count();
        pageVariables.put(USER_COUNT_VARIABLE_NAME, cachedUserCount);

        response.setContentType(CONTENT_TYPE);
        String page = templateProcessor.getPage(USER_PAGE_TEMPLATE, pageVariables);
        response.getWriter().println(page);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = PageVariables.create(request);

        cachedUserCount = (null == cachedUserCount) ? dbService.count() : cachedUserCount;
        pageVariables.put(USER_COUNT_VARIABLE_NAME, cachedUserCount);

        if (null != request.getParameter(SUBMIT_SEARCH_PARAMETER_NAME)) {
            String userId = request.getParameter(ID_PARAMETER_NAME);
            if (!userId.equals("")) {

                try {
                    String userName = dbService.read(Long.parseLong(userId)).getName();
                    pageVariables.put(USER_NAME_VARIABLE_NAME, userName);
                } catch (Exception e) {
                    e.printStackTrace();
                    pageVariables.put(USER_NAME_VARIABLE_NAME, USER_NOT_FOUND);

                }
            } else {
                pageVariables.put(IS_SEARCH_ERROR_VARIABLE_NAME, USER_NO_ID_SET);
            }
        }

        response.setContentType(CONTENT_TYPE);
        String page = templateProcessor.getPage(USER_PAGE_TEMPLATE, pageVariables);
        response.getWriter().println(page);
        response.setStatus(HttpServletResponse.SC_OK);
    }

}