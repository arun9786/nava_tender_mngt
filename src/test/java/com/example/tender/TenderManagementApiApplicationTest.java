package com.example.tender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.tender.dto.LoginDTO;
import com.example.tender.model.BiddingModel;
import com.example.tender.model.RoleModel;
import com.example.tender.model.UserModel;
import com.example.tender.repository.RoleRepository;
import com.example.tender.repository.UserRepository;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.System.out;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
class TenderManagementApiApplicationTest {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    private MockMvc mockMvc;

    private static final String TOKEN_APPROVER_1 = "token_approver_1";
    private static final String TOKEN_BIDDER_1   = "token_bidder_1";
    private static final String TOKEN_BIDDER_2   = "token_bidder_2";
    private static final String ID_USER_1   = "id_user_1";
    private static final String ID_USER_2   = "id_user_2";
    private static final String ID_BIDDING_1 = "id_bidding_1";
    private static final String ID_BIDDING_2 = "id_bidding_2";

    @Autowired
    WebApplicationContext context;

    @BeforeEach
    void setMockMvc(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }


    @Test
    void a_testFailedLoginAttempt() throws Exception {



        LoginDTO loginData = new LoginDTO("bidderemail@gmail.com", "wrongpassword");
        mockMvc.perform(post("/login")
                        .content(toJson(loginData)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }


    @Test
    void b_testSuccessLoginAttemptBidder() throws Exception {



        LoginDTO loginData = new LoginDTO("bidderemail@gmail.com", "bidder123$");
        MvcResult result = mockMvc.perform(post("/login")
                        .content(toJson(loginData)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        assert obj.has("jwt");
        assert obj.getInt("status")==200;
        saveDataToFileSystem(TOKEN_BIDDER_1, obj.getString("jwt"));




        LoginDTO loginData1 = new LoginDTO("bidderemail2@gmail.com", "bidder789$");
        MvcResult result1 = mockMvc.perform(post("/login")
                .content(toJson(loginData1)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        JSONObject obj1 = new JSONObject(result1.getResponse().getContentAsString());
        assert obj1.has("jwt");
        assert obj1.getInt("status")==200;
        saveDataToFileSystem(TOKEN_BIDDER_2, obj1.getString("jwt"));
    }


    @Test
    void c_checkSuccessLoginAttemptApprover() throws Exception {



        LoginDTO loginData = new LoginDTO("approveremail@gmail.com", "approver123$");

        MvcResult result = mockMvc.perform(post("/login")
                        .content(toJson(loginData)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
        JSONObject jsonUser1Response = new JSONObject(result.getResponse().getContentAsString());
        assert jsonUser1Response.has("jwt");
        assert jsonUser1Response.getInt("status")==200;
        saveDataToFileSystem(TOKEN_APPROVER_1, jsonUser1Response.getString("jwt"));
    }

    @Test
    void d_checkSuccessBiddingAdding() throws Exception {



        print("Testing bidder id1");
        print(String.valueOf(getDataFromFileSystem(TOKEN_BIDDER_1)));
        BiddingModel biddingModel = new BiddingModel(2608, 14000000.0, 2.6);
        MvcResult result = mockMvc.perform(post("/bidding/add")
                        .content(toJson(biddingModel))
                        .header("Authorization", "Bearer " + getDataFromFileSystem(TOKEN_BIDDER_1))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(201)).andReturn();
        print(result.getResponse().getContentAsString());




        BiddingModel biddingModel1 = new BiddingModel(3123, 17000000.0, 3.1);
        MvcResult result2 = mockMvc.perform(post("/bidding/add")
                .content(toJson(biddingModel1))
                .header("Authorization", "Bearer " + getDataFromFileSystem(TOKEN_BIDDER_1))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(201)).andReturn();

        print(result2.getResponse().getContentAsString());


        JSONObject response = new JSONObject(result.getResponse().getContentAsString());
        assert response.has("id");
        assert Objects.equals(response.getInt("biddingId"),2608);
        assert Objects.equals(response.getString("dateOfBidding"),gettime());
        assert Objects.equals(response.getString("status"),"pending");


        JSONObject response2 = new JSONObject(result2.getResponse().getContentAsString());
        assert response2.has("id");
        assert Objects.equals(response2.getInt("biddingId"),3123);
        assert Objects.equals(response2.getDouble("bidAmount"),17000000.0);
        assert Objects.equals(response2.getInt("bidderId"),1);


        saveDataToFileSystem(ID_BIDDING_1, response.getInt("id"));
        saveDataToFileSystem(ID_BIDDING_2, response2.getInt("id"));



    }

    @Test
    void e_checkFailedBiddingAdding() throws Exception {


        BiddingModel biddingModel = new BiddingModel(1142, 19000000.0, 5.0);

        // Unauthorized (no token)
        mockMvc.perform(post("/bidding/add")
                        .content(toJson(biddingModel))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());

        // Forbidden (wrong role)
        mockMvc.perform(post("/bidding/add")
                        .content(toJson(biddingModel))
                        .header("Authorization", "Bearer " + getDataFromFileSystem(TOKEN_APPROVER_1))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());

    }




    @Test
    void f_getSuccessBiddingCheckTest() throws Exception {



        mockMvc.perform(get("/bidding/list?bidAmount=15000000") .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + getDataFromFileSystem(TOKEN_APPROVER_1)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(2)))
                .andExpect(jsonPath("$[0].biddingId", Matchers.is(3123)))
                .andExpect(jsonPath("$[0].projectName", containsStringIgnoringCase("Metro Phase V 2024")))
                .andExpect(jsonPath("$[0].bidAmount", Matchers.is(17000000.0)))
                .andExpect(jsonPath("$[0].yearsToComplete", Matchers.is(3.1)))
                .andExpect(jsonPath("$[0].dateOfBidding", containsStringIgnoringCase(gettime())))
                .andExpect(jsonPath("$[0].status", containsStringIgnoringCase("pending")))
                .andExpect(jsonPath("$[0].bidderId", Matchers.is(1)));


    }

    @Test
    void g_getFailedBiddingCheckTest() throws Exception {



        mockMvc.perform(get("/bidding/list?bidAmount=31000000").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + getDataFromFileSystem(TOKEN_BIDDER_2)))
                .andExpect(MockMvcResultMatchers.status().is(400));


    }

    @Test
    void h_updateSuccessBiddingWithDetailsCheck() throws Exception {



        BiddingModel biddingModel = new BiddingModel("approved");
         MvcResult result = mockMvc.perform(patch("/bidding/update/" + getDataFromFileSystem(ID_BIDDING_1))
                        .content(toJson(biddingModel))
                        .header("Authorization", "Bearer " + getDataFromFileSystem(TOKEN_APPROVER_1))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(200)).andReturn();

        JSONObject response = new JSONObject(result.getResponse().getContentAsString());
        assert response.has("id");
        assert Objects.equals(response.getString("status"),"approved");
        assert Objects.equals(response.getInt("biddingId"),2608);


    }

    @Test
    void i_updateFailedBiddingWithDetailsCheck() throws Exception {

        BiddingModel biddingModel = new BiddingModel("approved");

        // Bidder cannot update

        MvcResult result = mockMvc.perform(patch("/bidding/update/" + getDataFromFileSystem(ID_BIDDING_2))
                        .content(toJson(biddingModel))
                        .header("Authorization", "Bearer " + getDataFromFileSystem(TOKEN_BIDDER_2))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(403)).andReturn();


        // Bad ID
        MvcResult result2 = mockMvc.perform(patch("/bidding/update/8")
                        .content(toJson(biddingModel))
                        .header("Authorization", "Bearer " + getDataFromFileSystem(TOKEN_APPROVER_1))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(400)).andReturn();



    }



    @Test
    void j_deleteBiddingWithNoAccess() throws Exception {



        mockMvc.perform(delete("/bidding/delete/" + getDataFromFileSystem(ID_BIDDING_1))
                                .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getDataFromFileSystem(TOKEN_BIDDER_2)))
                .andExpect(status().is(403))
                .andReturn();



        mockMvc.perform(delete("/bidding/delete/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getDataFromFileSystem(TOKEN_BIDDER_1)))
                .andExpect(status().is(400))
                .andReturn();

    }


    @Test
    void k_deleteBiddingWithAccessBidder() throws Exception {



        mockMvc.perform(delete("/bidding/delete/" + getDataFromFileSystem(ID_BIDDING_1))
                                .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getDataFromFileSystem(TOKEN_BIDDER_1)))
                .andExpect(status().is(204))
                .andReturn();


    }

    @Test
    void l_deleteBiddingWithAccessApprover() throws Exception {




        mockMvc.perform(delete("/bidding/delete/" + getDataFromFileSystem(ID_BIDDING_2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getDataFromFileSystem(TOKEN_APPROVER_1)))
                .andExpect(status().is(204))
                .andReturn();


    }

    @Test
    void z_checkSwagger() throws Exception {
         MvcResult result = mockMvc.perform(get("/v3/api-docs") .header("Authorization", "Bearer " + getDataFromFileSystem(TOKEN_BIDDER_1))).andExpect(status().isOk()).andReturn();
         assert result.getResponse().getContentAsString().contains("openapi");
    }


    private byte[] toJson(Object r) throws Exception {
        ObjectMapper map = new ObjectMapper();
        return map.writeValueAsString(r).getBytes();
    }


    private void print(String s){
        out.println(s);
    }

    private void saveDataToFileSystem(Object key, Object value) throws Exception {
        try {
            JSONObject jsonObject = new JSONObject();
            StringBuilder builder = new StringBuilder();
            try{
                File myObj = new File("temp.txt");
                Scanner myReader= new Scanner(myObj);
                while(myReader.hasNextLine()){
                    builder.append(myReader.nextLine());
                }
                myReader.close();
                if(!builder.toString().isEmpty())
                    jsonObject = new JSONObject(builder.toString());
            } catch (Exception ex){
                ex.printStackTrace();
            }

            BufferedWriter writer= new BufferedWriter(new FileWriter("temp.txt"));
            jsonObject.put((String) key, value);
            writer.write(jsonObject.toString());
            writer.close();
        } catch (Exception ex){
            throw new Exception("Data not saved");
        }
    }

    private Object getDataFromFileSystem(String key) throws Exception {
        try{
            File myObj = new File("temp.txt");
            Scanner myReader= new Scanner(myObj);
            StringBuilder builder = new StringBuilder();
            while(myReader.hasNextLine()){
                builder.append(myReader.nextLine());
            }
            myReader.close();
            JSONObject jsonObject = new JSONObject(builder.toString());
            return jsonObject.get(key);
        } catch (Exception ex){
            throw new Exception("Data not found. Check authentication and ID generations to make sure data is being produced.");
        }
    }



    private String gettime() {
        String x = String.valueOf(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        long milliseconds = Long.parseLong(x);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return formatter.format(calendar.getTime());
    }

}