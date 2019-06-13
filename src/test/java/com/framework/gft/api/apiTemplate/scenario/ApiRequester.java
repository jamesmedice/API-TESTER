package com.framework.gft.api.apiTemplate.scenario;

import org.apache.http.entity.ContentType;
import org.junit.Assert;

import com.framework.gft.api.apiTemplate.HttpActions;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * 
 * @author a73s
 *
 */
public class ApiRequester {

	HttpActions actions;

	@Given("^the host url \"(.*)\"$")
	public void set_host_url(String url) {
		actions = new HttpActions(url);
	}

	@When("^call get api$")
	public void get_call_api() {
		actions.get(null);
	}

	@When("^call post api - json file \"(.*)\"$")
	public void post_call_api(String json) {
		actions.post(json, null);
	}

	@Then("^http response JSON$")
	public void response_format_JSON() {
		Assert.assertEquals(actions.getContentType(), ContentType.APPLICATION_JSON);
	}

	@Then("^http response status code (\\d+)$")
	public void http_response_status_code(int code) {
		Assert.assertEquals(actions.getStatusCode(), code);
	}

	@Given("^the route host url \"(.*)\"$")
	public void set_router_host_url(String url) {
		actions = new HttpActions(url, true);
	}

	@When("^route call get api$")
	public void router_get_call_api() {
		actions.routerGet();
	}

	@When("^route call post api - json file \"(.*)\"$")
	public void router_post_call_api(String json) {
		actions.routerPost(json);
	}

	@Then("^route http response payload not empty$")
	public void router_http_response_payload() {
		Assert.assertEquals(actions.getRouterEntity().isEmpty(), false);
	}

	@Then("^route http response status code (\\d+)$")
	public void router_http_response_status_code(int code) {
		Assert.assertEquals(actions.getRouterStatusCode(), code);
	}
}
