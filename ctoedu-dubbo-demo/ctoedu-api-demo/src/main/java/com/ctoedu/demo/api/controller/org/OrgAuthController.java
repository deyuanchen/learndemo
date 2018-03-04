package com.ctoedu.demo.api.controller.org;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.ctoedu.common.vo.ViewerResult;
import com.ctoedu.demo.api.service.auth.AuthService;
import com.ctoedu.demo.api.service.auth.AuthedService;
import com.ctoedu.demo.facade.Principal;
import com.ctoedu.demo.facade.Resource;
import com.ctoedu.demo.facade.auth.entity.UmsAcl;
import com.ctoedu.demo.facade.auth.service.UmsAuthFacade;

@Controller
@RequestMapping("/api/org/auth")
public class OrgAuthController {
	
	@Reference
	private UmsAuthFacade umsAuthFacade;
	
	@Autowired
	private AuthService authService;

	@Autowired
	private AuthedService authedService;
	
	@RequestMapping(value="/findMenu", method=RequestMethod.POST)
	public @ResponseBody ViewerResult findMenuForAuthTree(HttpServletRequest request, @RequestBody JSONObject obj){
		ViewerResult result = new ViewerResult();
		try {
			String appId = obj.getString("appId");
			String dataId = obj.getString("dataId");
			Object currentUsername = request.getAttribute("currentUsername");
			JSONObject jsonObj = authService.find(currentUsername, appId, null, dataId, Principal.PRINCIPAL_ORG, Resource.RESOURCE_MENU);
			result.setSuccess(true);
			result.setData(jsonObj);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrMessage("获取菜单资源异常");
			e.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping(value="/findAuthedMenu", method=RequestMethod.POST)
	public @ResponseBody ViewerResult findAuthedMenu(HttpServletRequest request, @RequestBody JSONObject obj){
		ViewerResult result = new ViewerResult();
		try {
			Object currentUsername = request.getAttribute("currentUsername");
			if(currentUsername == null){
				result.setData(new String[0]);
			}else{
		        String orgId = obj.getString("dataId");
				List<UmsAcl> aclList = umsAuthFacade.findAcl(orgId, Principal.PRINCIPAL_ORG, Resource.RESOURCE_MENU);
				int size = aclList.size();
				String[] menuIds = new String[size];
				for(int i = 0; i < size; i++){
					UmsAcl acl = aclList.get(i);
					String rid = acl.getRid();
					menuIds[i] = rid;
				}
				result.setData(menuIds);
			}
			result.setSuccess(true);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrMessage(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping(value="/addMenu", method=RequestMethod.POST)
	public @ResponseBody ViewerResult addMenu(@RequestBody JSONObject obj){
		ViewerResult result = new ViewerResult();
		try {
			//get JSON format parameters
			String orgId = obj.getString("dataId");
			String menuId = obj.getString("menuId");
			boolean checked = obj.getBoolean("checked");
			if(checked){
				umsAuthFacade.addPrincipalAcl(orgId, Principal.PRINCIPAL_ORG, menuId, Resource.RESOURCE_MENU);
			}else{
				umsAuthFacade.deletePrincipalAcl(orgId, Principal.PRINCIPAL_ORG, menuId, Resource.RESOURCE_MENU);
			}
			result.setSuccess(true);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrMessage(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/addMenus", method=RequestMethod.POST)
	public @ResponseBody ViewerResult addMenus(@RequestBody JSONObject obj){
		ViewerResult result = new ViewerResult();
		try {
			//get JSON format parameters
			String orgId = obj.getString("dataId");
			List<String> list = (List<String>) obj.get("menuIds");
			String[] menuIds = new String[list.size()];
			list.toArray(menuIds);
			umsAuthFacade.addPrincipalAcl(orgId, Principal.PRINCIPAL_ORG, menuIds, Resource.RESOURCE_MENU, null);
			result.setSuccess(true);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrMessage(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping(value="/forbiddenMenu", method=RequestMethod.POST)
	public @ResponseBody ViewerResult forbiddenMenu(@RequestBody JSONObject obj){
		ViewerResult result = new ViewerResult();
		try {
			String orgId = obj.getString("dataId");
			String menuId = obj.getString("menuId");
			boolean checked = obj.getBoolean("checked");
			if(checked){
				umsAuthFacade.forbiddenPrincipalAcl(orgId, Principal.PRINCIPAL_ORG, menuId, Resource.RESOURCE_MENU);
			}else{
				umsAuthFacade.deleteForbiddenPrincipalAcl(orgId, Principal.PRINCIPAL_ORG, menuId, Resource.RESOURCE_MENU);
			}
			result.setSuccess(true);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrMessage(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * get all controller by conditions for auth
	 * @return
	 */
	@RequestMapping(value="/findController", method=RequestMethod.POST)
	public @ResponseBody ViewerResult findControllerForAuthTree(HttpServletRequest request, @RequestBody JSONObject obj){
		ViewerResult result = new ViewerResult();
		try {
			String appId = obj.getString("appId");
			String dataId = obj.getString("dataId");
			String menuId = obj.getString("menuId");
			Object currentUsername = request.getAttribute("currentUsername");
			JSONObject jsonObj = authService.find(currentUsername, appId, menuId, dataId, Principal.PRINCIPAL_ORG, Resource.RESOURCE_CONTROLLER);
			result.setSuccess(true);
			result.setData(jsonObj);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrMessage("获取请求资源异常");
			e.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping(value="/findAuthedController", method=RequestMethod.POST)
	public @ResponseBody ViewerResult findAuthedController(HttpServletRequest request, @RequestBody JSONObject obj){
		ViewerResult result = new ViewerResult();
		try {
			Object currentUsername = request.getAttribute("currentUsername");
			if(currentUsername == null){
				result.setData(new String[0]);
			}else{
		        String orgId = obj.getString("dataId");
				List<UmsAcl> aclList = umsAuthFacade.findAcl(orgId, Principal.PRINCIPAL_ORG, Resource.RESOURCE_CONTROLLER);
				int size = aclList.size();
				String[] controllerIds = new String[size];
				for(int i = 0; i < size; i++){
					controllerIds[i] = aclList.get(i).getRid();
				}
				result.setData(controllerIds);
			}
			result.setSuccess(true);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrMessage(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping(value="/addControllerResource", method=RequestMethod.POST)
	public @ResponseBody ViewerResult addControllerResource(@RequestBody JSONObject obj){
		ViewerResult result = new ViewerResult();
		try {
			//get JSON format parameters
			String orgId = obj.getString("dataId");
			String controllerId = obj.getString("controllerId");
			boolean checked = obj.getBoolean("checked");
			if(checked){
				umsAuthFacade.addPrincipalAcl(orgId, Principal.PRINCIPAL_ORG, controllerId, Resource.RESOURCE_CONTROLLER);
			}else{
				umsAuthFacade.deletePrincipalAcl(orgId, Principal.PRINCIPAL_ORG, controllerId, Resource.RESOURCE_CONTROLLER);
			}
			result.setSuccess(true);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrMessage(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping(value="/forbiddenController", method=RequestMethod.POST)
	public @ResponseBody ViewerResult forbiddenController(@RequestBody JSONObject obj){
		ViewerResult result = new ViewerResult();
		try {
			String orgId = obj.getString("dataId");
			String controllerId = obj.getString("controllerId");
			boolean checked = obj.getBoolean("checked");
			if(checked){
				umsAuthFacade.forbiddenPrincipalAcl(orgId, Principal.PRINCIPAL_ORG, controllerId, Resource.RESOURCE_CONTROLLER);
			}else{
				umsAuthFacade.deleteForbiddenPrincipalAcl(orgId, Principal.PRINCIPAL_ORG, controllerId, Resource.RESOURCE_CONTROLLER);
			}
			result.setSuccess(true);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrMessage(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/addControllerResources", method=RequestMethod.POST)
	public @ResponseBody ViewerResult addControllerResources(@RequestBody JSONObject obj){
		ViewerResult result = new ViewerResult();
		try {
			//get JSON format parameters
			String orgId = obj.getString("dataId");
			List<String> list = (List<String>) obj.get("controllerIds");
			String[] controllerIds = new String[list.size()];
			list.toArray(controllerIds);
			umsAuthFacade.addPrincipalAcl(orgId, Principal.PRINCIPAL_ORG, controllerIds, Resource.RESOURCE_CONTROLLER, null);
			result.setSuccess(true);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrMessage(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping(value="/findAuthedResource", method=RequestMethod.POST)
	public @ResponseBody ViewerResult findAuthedResource(HttpServletRequest request, @RequestBody JSONObject obj){
		ViewerResult result = new ViewerResult();
		try {
			Object currentUsername = request.getAttribute("currentUsername");
			if(currentUsername == null){
				result.setData(new String[0]);
			}else{
				String dataId = obj.getString("dataId");
				Map<String, Set<String>> resourcesMap = authedService.find(dataId, Principal.PRINCIPAL_ORG);
				result.setData(resourcesMap);
			}
			result.setSuccess(true);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrMessage(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
}