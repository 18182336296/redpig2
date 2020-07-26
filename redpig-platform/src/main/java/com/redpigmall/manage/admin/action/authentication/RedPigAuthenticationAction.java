package com.redpigmall.manage.admin.action.authentication;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.domain.Authentication;
import com.redpigmall.domain.User;
import com.redpigmall.manage.admin.action.base.BaseAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * @Auther: Administrator
 * @Date: 2018/9/15 16:54
 * @Description: 认证信息
 */
@Controller
public class RedPigAuthenticationAction extends BaseAction {
    /**
     * 跳转页面到认证管理
     *
     * @param request
     * @param response
     * @param currentPage
     * @return
     */
    @SecurityMapping(title = "认证管理", value = "/authentication_set*", rtype = "admin", rname = "认证管理", rcode = "authentication_set", rgroup = "认证管理")
    @RequestMapping({"/authentication_set"})
    public ModelAndView authentication_set(HttpServletRequest request,
                                                       HttpServletResponse response, String currentPage) {
        ModelAndView mv = new RedPigJModelAndView(
                "admin/blue/authentication/authentication_list.html",
                this.configService.getSysConfig(),
                //登录用户信息
                this.userConfigService.getUserConfig(), 0, request, response);
        return mv;
    }

    /**
     * 添加认证信息页面
     *
     * @param request
     * @param response
     * @param currentPage
     * @return
     */
    @SecurityMapping(title = "添加认证信息页面", value = "/authentication_add*", rtype = "admin", rname = "添加认证信息页面", rcode = "authentication_set", rgroup = "认证管理")
    @RequestMapping({"/authentication_add"})
    public ModelAndView authentication_add(HttpServletRequest request,
                                                       HttpServletResponse response, String currentPage, String reslout) {
        ModelAndView mv = new RedPigJModelAndView(
                "admin/blue/authentication/authentication_add.html",
                this.configService.getSysConfig(),
                //登录用户信息
                this.userConfigService.getUserConfig(), 0, request, response);
        mv.addObject("flag", true);
        return mv;
    }

    /**
     * 添加认证信息
     *
     * @param request
     * @param response
     * @param
     * @return
     */
    @SecurityMapping(title = "添加认证信息", value = "/authentication_save*", rtype = "admin", rname = "添加认证信息", rcode = "authentication_set", rgroup = "认证管理")
    @RequestMapping({"/authentication_save"})
    public String authentication_save(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  String company, String department, String position, String remark) {
        Authentication authentication = new Authentication();

        //获取登陆用户的信息
        User currentUser = SecurityUserHolder.getCurrentUser();
        if (StringUtils.isNotBlank(company)) {
            authentication.setCompany(company);
        }
        if (StringUtils.isNotBlank(department)) {
            authentication.setDepartment(department);
        }
        if (StringUtils.isNotBlank(position)) {
            authentication.setPosition(position);
        }
        if (StringUtils.isNotBlank(remark)) {
            authentication.setRemark(remark);
        }
        authentication.setName(currentUser.getTrueName());
        //因为编辑显示的问题，这里存就只接处理了
        String telephone = currentUser.getTelephone();
        String phone = telephone.replace(telephone.substring(3, (telephone.length() - 3)), "*****");
        authentication.setPhone(phone);
        String cardOld = authentication.getCard();
        String card = cardOld.replace(cardOld.substring(3, (cardOld.length() - 3)), "**********");
        authentication.setCard(card);
        authentication.setAddTime(new Date());

        //要返回插入数据的id...
        redPigAuthenticationService.saveEntity(authentication);
        //或取用户ID存到这个表里面的user_id中去

        authentication.setUser_id(currentUser.getId());
        authentication.setId(authentication.getId());
        redPigAuthenticationService.updateById(authentication);

        return "redirect:redPigAuthenticationAction_list?flag="+true;
    }

    /**
     * 查询一条数据
     *
     * @param request
     * @param response
     * @param
     * @return
     */
    @SecurityMapping(title = "查询一条数据", value = "/authentication_selectOne*", rtype = "admin", rname = "查询一条数据", rcode = "authentication_set", rgroup = "认证管理")
    @RequestMapping({"/authentication_selectOne"})
    public ModelAndView authentication_selectOne(HttpServletRequest request,
                                                             HttpServletResponse response, String currentPage,
                                                             String id) {
        ModelAndView mv = new RedPigJModelAndView(
                "admin/blue/authentication/authentication.html",
                this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 0, request, response);
        if (StringUtils.isNotBlank(id)) {
            Authentication authentication = redPigAuthenticationService.selectByPrimaryKey(Long.parseLong(id));
            mv.addObject("objs", authentication);
            mv.addObject("flag", false);
        }
        return mv;
    }

    /**
     * 编辑认证信息
     *
     * @param request
     * @param response
     * @param
     * @return
     */
    @SecurityMapping(title = "编辑认证信息", value = "/authentication_update*", rtype = "admin", rname = "编辑认证信息", rcode = "authentication_set", rgroup = "认证管理")
    @RequestMapping({"/authentication_update"})
    public String authentication_update(HttpServletRequest request,
                                                    HttpServletResponse response, String currentPage,
                                                    String id, String name, String phone, String car, String company, String department, String position, String remark) {
        ModelAndView mv = new RedPigJModelAndView(
                "admin/blue/authentication/authentication_list.html",
                this.configService.getSysConfig(),
                //登录用户信息
                this.userConfigService.getUserConfig(), 0, request, response);
        Authentication authentication = new Authentication();
        //暂时不做User还有session数据同步

        if (StringUtils.isNotBlank(id)) {
            authentication.setId(Long.parseLong(id));
        }
        if (StringUtils.isNotBlank(name)) {
            authentication.setName(name);
        }
        if (StringUtils.isNotBlank(phone)) {
            authentication.setPhone(phone);
        }
        if (StringUtils.isNotBlank(car)) {
            authentication.setCard(car);
        }
        if (StringUtils.isNotBlank(company)) {
            authentication.setCompany(company);
        }
        if (StringUtils.isNotBlank(department)) {
            authentication.setDepartment(department);
        }
        if (StringUtils.isNotBlank(position)) {
            authentication.setPosition(position);
        }
        if (StringUtils.isNotBlank(remark)) {
            authentication.setRemark(remark);
        }
        authentication.setUpdate_time(new Date());

        redPigAuthenticationService.updateById(authentication);

        return "redirect:authentication_list?currentPage=" + currentPage;
    }

    /**
     * 认证信息列表
     *
     * @param request
     * @param response
     * @return
     */
      @SecurityMapping(title = "认证信息列表", value = "/authentication_list*", rtype = "admin", rname = "认证信息列表", rcode = "authentication_set", rgroup = "认证管理")
    @RequestMapping({"/authentication_list"})
    public ModelAndView authentication_list(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage, String orderBy,
                                            String orderType, String name, String phone, String card, String company, String beginTime, String endTime,String flag) {
        ModelAndView mv = new RedPigJModelAndView(
                "admin/blue/authentication/authentication_list.html",
                this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 0, request, response);


        String url = this.redPigSysConfigService.getSysConfig().getAddress();

        mv.addObject("name", name);
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }

        String params = "";

        Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
        if (StringUtils.isNotBlank(name) ) {
            maps.put("name", name);
        }
        if (StringUtils.isNotBlank(beginTime)) {
            maps.put("beginTime", CommUtil.formatDate(beginTime));
        }
        if (StringUtils.isNotBlank(endTime)) {
            maps.put("endTime", CommUtil.formatDate(endTime));
        }

        if (StringUtils.isNotBlank(phone)) {
            maps.put("phone", phone);
        }
        if (StringUtils.isNotBlank(card)) {
            maps.put("card", card);
        }
        if (StringUtils.isNotBlank(company)) {
            maps.put("company", company);
        }
               IPageList pList = this.redPigAuthenticationService.list(maps);
          CommUtil.saveIPageList2ModelAndView(url + "/authentication/authentication_list.html", "", params, pList, mv);

          if (StringUtils.isNotBlank(flag) && flag.equals("true")){

        mv.addObject("flag",flag);
          }

        return mv;
    }



    /**
     * 编辑认证信息状态
     *
     * @param request
     * @param response
     * @param
     * @return
     */
    @SecurityMapping(title = "编辑认证信息状态", value = "/authentication_updateOne*", rtype = "admin", rname = "编辑认证信息状态", rcode = "authentication_set", rgroup = "认证管理")
    @RequestMapping({"/authentication_updateOne"})
    public String authentication_updateOne(HttpServletRequest request,
                                                       HttpServletResponse response, String currentPage,
                                                       String id, String state) {

        Authentication authentication = new Authentication();
        //暂时不做User还有session数据同步

        if (StringUtils.isNotBlank(id)) {
            authentication.setId(Long.parseLong(id));
        }
        if (StringUtils.isNotBlank(state)) {
            if (state.endsWith("0")){
                authentication.setState(0);
            }else {
                authentication.setState(1);
            }
        }

        authentication.setUpdate_time(new Date());

        redPigAuthenticationService.updateById(authentication);

        return "redirect:authentication_list?currentPage=" + currentPage;
    }
    /**
     * 编辑认证信息审核状态
     *
     * @param request
     * @param response
     * @param
     * @return
     */
    @SecurityMapping(title = "编辑认证信息审核状态", value = "/authentication_updateTwo*", rtype = "admin", rname = "编辑认证信息审核状态", rcode = "authentication_set", rgroup = "认证管理")
    @RequestMapping({"/authentication_updateTwo"})
    public String authentication_updateTwo(HttpServletRequest request,
                                                       HttpServletResponse response, String currentPage,
                                                       String id, String examine_state) {

        Authentication authentication = new Authentication();
        //暂时不做User还有session数据同步

        if (StringUtils.isNotBlank(examine_state)) {
            if (examine_state.equals("0")){
                authentication.setExamine_state(0);
            }else {
                authentication.setExamine_state(1);
            }
        }
        authentication.setUpdate_time(new Date());
        if (StringUtils.isNotBlank(id)  && id.length()>1){
            String[] split = id.split(",");
            for(String s : split){
                authentication.setId(Long.parseLong(s));
                redPigAuthenticationService.updateById(authentication);
            }
        }else {
            authentication.setId(Long.parseLong(id));
            redPigAuthenticationService.updateById(authentication);
        }

        return "redirect:authentication_list?currentPage=" + currentPage;
    }

}
