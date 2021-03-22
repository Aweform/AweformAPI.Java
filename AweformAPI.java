package com.aweform;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//
// AweformAPI
////////////////////////////////////////////////////////////////////////////////////

public class AweformAPI {

    public class AweformException extends Exception {

        public AweformException(String message) {

            super(message);
        }
    }

    private String APIKey = "";
    private String APIURL = "";

    public AweformAPI(String apiKey) throws AweformException {

        this(apiKey, "https://aweform.com/api");
    }

    public AweformAPI(String apiKey, String apiURL) throws AweformException {

        if (apiKey == null || apiKey.equals("")) { throw new AweformException("You must provide an API key"); }
        if (apiURL == null || apiURL.equals("")) { throw new AweformException("You must provide the API URL"); }

        APIKey = apiKey;
        APIURL = apiURL;
    }

    public AweformUser getMe() throws Exception {

        return aweformUserFromJSON(request("/me/"));
    }

    public AweformResponse getResponse(Long responseId) throws Exception {

        return aweformResponseFromJSON(request("/response/" + responseId + "/"));
    }

    public List<AweformResponse> getResponsesForForm(Long formId) throws Exception {

        return getResponsesForForm(formId, 0, 100);
    }

    public List<AweformResponse> getResponsesForForm(Long formId, Integer from, Integer count) throws Exception {

        return aweformResponsesFromJSON(requestPaged("/form/" + formId + "/responses/", from, count));
    }

    public List<AweformResponse> getResponsesInWorkspace(Long workspaceId) throws Exception {

        return getResponsesInWorkspace(workspaceId, 0, 100);
    }

    public List<AweformResponse> getResponsesInWorkspace(Long workspaceId, Integer from, Integer count) throws Exception {

        return aweformResponsesFromJSON(requestPaged("/workspace/" + workspaceId + "/responses/", from, count));
    }

    public List<AweformResponse> getResponses() throws Exception {

        return getResponses(0, 100);
    }

    public List<AweformResponse> getResponses(Integer from, Integer count) throws Exception {

        return aweformResponsesFromJSON(requestPaged("/responses/", from, count));
    }

    public AweformFormDefinition getFormDefinitionForForm(Long formId) throws Exception {

        return aweformFormDefinitionFromJSON(request("/form/" + formId + "/formDefinition/"));
    }

    public List<AweformFormDefinition> getFormDefinitionsInWorkspace(Long workspaceId) throws Exception {

        return getFormDefinitionsInWorkspace(workspaceId, 0, 100);
    }

    public List<AweformFormDefinition> getFormDefinitionsInWorkspace(Long workspaceId, Integer from, Integer count) throws Exception {

        return aweformFormDefinitionsFromJSON(requestPaged("/workspace/" + workspaceId + "/formDefinitions/", from, count));
    }

    public List<AweformFormDefinition> getFormDefinitions() throws Exception {

        return getFormDefinitions(0, 100);
    }

    public List<AweformFormDefinition> getFormDefinitions(Integer from, Integer count) throws Exception {

        return aweformFormDefinitionsFromJSON(requestPaged("/formDefinitions/", from, count));
    }

    public AweformForm getForm(Long formId) throws Exception {

        return aweformFormFromJSON(request("/form/" + formId + "/"));
    }

    public List<AweformForm> getFormsInWorkspace(Long workspaceId) throws Exception {

        return getFormsInWorkspace(workspaceId, 0, 100);
    }

    public List<AweformForm> getFormsInWorkspace(Long workspaceId, Integer from, Integer count) throws Exception {

        return aweformFormsFromJSON(requestPaged("/workspace/" + workspaceId + "/forms/", from, count));
    }

    public List<AweformForm> getForms() throws Exception {

        return getForms(0, 100);
    }

    public List<AweformForm> getForms(Integer from, Integer count) throws Exception {

        return aweformFormsFromJSON(requestPaged("/forms/", from, count));
    }

    public AweformWorkspace getWorkspace(Long workspaceId) throws Exception {

        return aweformWorkspaceFromJSON(request("/workspace/" + workspaceId + "/"));
    }

    public List<AweformWorkspace> getWorkspaces() throws Exception {

        return getWorkspaces(0,100);
    }

    public List<AweformWorkspace> getWorkspaces(Integer from, Integer count) throws Exception {

        return aweformWorkspacesFromJSON(requestPaged("/workspaces/", from, count));
    }

    private AweformUser aweformUserFromJSON(AweformJSON.Element json) {

        AweformUser user = new AweformUser();
        user.id = json.getAttributeAsLong("id");
        user.emailAddress = json.getAttributeAsString("emailAddress");
        user.name = json.getAttributeAsString("name");
        user.accountType = json.getAttributeAsString("accountType");

        return user;
    }

    private List<AweformForm> aweformFormsFromJSON(AweformJSON.Element json) {

        List<AweformForm> forms = new ArrayList<AweformForm>();

        for (AweformJSON.Element jsonForm : json.getItems()) {

            forms.add(aweformFormFromJSON(jsonForm));
        }

        return forms;
    }

    private AweformForm aweformFormFromJSON(AweformJSON.Element json) {

        AweformForm form = new AweformForm();
        form.id = json.getAttributeAsLong("id");
        form.name = json.getAttributeAsString("name");
        form.workspaceId = json.getAttributeAsLong("workspaceId");

        return form;
    }

    private List<AweformWorkspace> aweformWorkspacesFromJSON(AweformJSON.Element json) {

        List<AweformWorkspace> workspaces = new ArrayList<AweformWorkspace>();

        for (AweformJSON.Element jsonForm : json.getItems()) {

            workspaces.add(aweformWorkspaceFromJSON(jsonForm));
        }

        return workspaces;
    }

    private AweformWorkspace aweformWorkspaceFromJSON(AweformJSON.Element json) {

        AweformWorkspace workspace = new AweformWorkspace();
        workspace.id = json.getAttributeAsLong("id");
        workspace.name = json.getAttributeAsString("name");

        return workspace;
    }

    private List<AweformResponse> aweformResponsesFromJSON(AweformJSON.Element json) throws Exception {

        List<AweformResponse> responses = new ArrayList<AweformResponse>();

        for (AweformJSON.Element jsonForm : json.getItems()) {

            responses.add(aweformResponseFromJSON(jsonForm));
        }

        return responses;
    }

    private AweformResponse aweformResponseFromJSON(AweformJSON.Element json) throws Exception {

        AweformResponse response = new AweformResponse();
        response.id = json.getAttributeAsLong("id");
        response.dateInUtc = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(json.getAttributeAsString("dateInUTC"));
        response.formId = json.getAttributeAsLong("formId");
        response.workspaceId = json.getAttributeAsLong("workspaceId");

        List<AweformJSON.Element> attributes = json.getAttributes();

        for (AweformJSON.Element attribute : attributes) {

            if (attribute.name.equals("id") || attribute.name.equals("dateInUTC") || attribute.name.equals("formId") || attribute.name.equals("workspaceId")) {

                continue;
            }

            AweformQuestionAndAnswer questionAndAnswer = new AweformQuestionAndAnswer();
            questionAndAnswer.question = attribute.name;
            questionAndAnswer.answer = attribute.value;

            response.answers.add(questionAndAnswer);
        }

        return response;
    }

    private List<AweformFormDefinition> aweformFormDefinitionsFromJSON(AweformJSON.Element json) {

        List<AweformFormDefinition> formsDefinitions = new ArrayList<AweformFormDefinition>();

        for (AweformJSON.Element jsonForm : json.getItems()) {

            formsDefinitions.add(aweformFormDefinitionFromJSON(jsonForm));
        }

        return formsDefinitions;
    }

    private AweformFormDefinition aweformFormDefinitionFromJSON(AweformJSON.Element json) {

        AweformFormDefinition formDefinition = new AweformFormDefinition();
        formDefinition.id = json.getAttributeAsLong("id");
        formDefinition.name = json.getAttributeAsString("name");
        formDefinition.workspaceId = json.getAttributeAsLong("workspaceId");

        AweformJSON.Element components = json.getAttribute("components");

        for (AweformJSON.Element component : components.getItems()) {

            AweformFormComponent formComponent = new AweformFormComponent();
            formComponent.name = component.getAttributeAsString("name");
            formComponent.type = AweformFormComponentType.valueOf(component.getAttributeAsString("type"));

            if (formComponent.type == AweformFormComponentType.PictureChoice || formComponent.type == AweformFormComponentType.Rating || formComponent.type == AweformFormComponentType.Select || formComponent.type == AweformFormComponentType.YesNo) {

                formComponent.options = new ArrayList<String>();

                AweformJSON.Element options = component.getAttribute("options");

                for (AweformJSON.Element option : options.getItems()) {

                    formComponent.options.add(option.value);
                }
            }

            formDefinition.components.add(formComponent);
        }

        return formDefinition;
    }

    private AweformJSON.Element requestPaged(String url, Integer from, Integer count) throws Exception {

        Integer startPage = (from / 100);
        Integer endPage = ((from + count - 1) / 100);

        Integer startRequestIndex = (startPage * 100);

        Integer page = startPage;

        AweformJSON.Element paged = null;

        while (page <= endPage) {

            AweformJSON.Element singlePage = request(url + "?page=" + page);

            if (paged == null) {

                paged = singlePage;

            } else {

                for (AweformJSON.Element component : singlePage.getItems()) {

                    paged.elements.add(component);
                }
            }

            if (singlePage.elements.size() == 0) {

                break;
            }

            page += 1;
        }

        from -= startRequestIndex;

        if (from > 0) {

            paged.elements.subList(0, from).clear();
        }

        if (count < paged.elements.size()) {

            paged.elements.subList(count, paged.elements.size() - count).clear();
        }

        if (paged == null) {

            throw new AweformException("Zero pages returned");
        }

        return paged;
    }

    protected AweformJSON.Element request(String url) throws Exception {

        String fullURL = APIURL + url + (url.contains("?") ? "&" : "?") + "apiKey=" + APIKey;

        URL urlObject = new URL(fullURL);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String inputLine;
        StringBuffer content = new StringBuffer();

        while ((inputLine = bufferedReader.readLine()) != null) {

            content.append(inputLine);
        }

        bufferedReader.close();

        connection.disconnect();

        AweformJSON aweformJSON = new AweformJSON();
        AweformJSON.Element element = aweformJSON.parse(content.toString());

        if (element.getAttribute("error") != null) {

            throw new AweformException(element.getAttributeAsString("error"));
        }

        return element;
    }

    public class AweformUser {

        public Long id;
        public String emailAddress;
        public String name;
        public String accountType;
    }

    public class AweformWorkspace {

        public Long id;
        public String name;
    }

    public class AweformForm {

        public Long id;
        public String name;
        public Long workspaceId;
    }

    public class AweformResponse {

        public Long id;
        public Date dateInUtc;
        public Long formId;
        public Long workspaceId;
        public List<AweformQuestionAndAnswer> answers;

        public AweformResponse() {

            answers = new ArrayList<AweformQuestionAndAnswer>();
        }
    }

    public class AweformQuestionAndAnswer {

        public String question;
        public String answer;
    }

    public class AweformFormDefinition {

        public Long id;
        public String name;
        public Long workspaceId;
        public List<AweformFormComponent> components;

        public AweformFormDefinition() {

            components = new ArrayList<AweformFormComponent>();
        }
    }

    public enum AweformFormComponentType {

        HiddenValue,
        ShortText,
        LongText,
        Number,
        Select,
        Date,
        Email,
        Screen,
        Rating,
        EndScreen,
        PictureChoice,
        YesNo
    }

    public class AweformFormComponent {

        public AweformFormComponentType type;
        public String name;
        public List<String> options;

        public AweformFormComponent() {

            options = null;
        }
    }
}
