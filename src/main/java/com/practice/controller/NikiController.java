package com.practice.controller;

import com.alibaba.fastjson.JSONObject;
import com.practice.entity.User;
import com.practice.entity.WxInfo;
import com.practice.service.NikiService;
import com.practice.util.HttpGetUtil;
import com.practice.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @MethodName: $
 * @Description: 获取微信openid
 * @Param: $
 * @Return: $
 * @Author: zhangliqian
 * @Date: $
 */
@RestController
@Slf4j
public class NikiController {
    @Autowired
    NikiService nikiService;


//    @Autowired
//    WxMpService wxMpService;

    @RequestMapping(value = "/getNiki", method = RequestMethod.GET)
    public User getNiki() {
        String niki = nikiService.getNiki();
        User user = new User();
        user.setId(1);
        RedisUtil.set("niki_2020", "我爱高倩");
        String niki_2020 = RedisUtil.get("niki_2020").toString();
        log.info("key niki_2020的值是 {}", niki_2020);
        user.setUserName(niki_2020);
        user.setPassWord("nikijingjing2020");
        return user;
    }

    /**
     * 测试重定向到登录页面
     http://niki.nat300.top/testRedirect?url=http://niki.nat300.top/getNiki
     * @param response
     * @param url
     * @throws IOException
     */
    @RequestMapping(value = "/testRedirect", method = RequestMethod.GET)
    public JSONObject testRedirect(HttpServletResponse response, @RequestParam("url") String url) throws IOException {
        JSONObject object = new JSONObject();
        response.setCharacterEncoding("UTF-8");
        response.sendRedirect(url);
        object.put("msg", "用户未登录");
        return object;
     }

    /**
     * 微信回调
     *
     * @param request
     * @param response
     */
    @RequestMapping("/callBack")
    public void callBack(HttpServletRequest request, HttpServletResponse response) {
        log.info("==>NikiController.callBack, req={}", request.getLocalPort());
        try {
            // 调用微信授权跳转获取openid
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            request.setCharacterEncoding("UTF-8");
            //这里要将你的授权回调地址处理一下，否则微信识别不了
            String redirect_uri = URLEncoder.encode("http://niki.nat300.top/cat/r", "UTF-8");
            //简单获取openid的话参数response_type与scope与state参数固定写死即可
            StringBuffer url = new StringBuffer("https://open.weixin.qq.com/connect/oauth2/authorize?redirect_uri=" + redirect_uri + "&appid=wxb00b277049d87059&response_type=code&scope=snsapi_base&state=1#wechat_redirect");
            log.info("==> url={}", url);
            //这里千万不要使用get请求，单纯的将页面跳转到该url即可
            response.sendRedirect(url.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/getCode")
    public void getCode(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?redirect_uri=http://niki.nat300.top/cat/r&appid=wxb00b277049d87059&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
        String result = HttpGetUtil.httpRequestToString(url, null);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String code = jsonObject.getString("code");
        log.info("==> 获取的code={}" + code);
    }

    @GetMapping("/auth")
    public void auth(@RequestParam("code") String code) {
        log.info("进入auth方法。。。");
        log.info("code={}", code);
    }


    @RequestMapping("/getOpenId")
    public void getOpenId(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String code = request.getHeader("code");
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        Map params = new HashMap();
        params.put("appid", "wxb00b277049d87059");
        params.put("secret", "4f407849f4b50854ff6fbec3cc3d28a6");
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        String result = HttpGetUtil.httpRequestToString(
                "https://api.weixin.qq.com/sns/oauth2/access_token", params);
        if (result != null) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String openid = jsonObject.get("openid").toString();
            log.info("==> 获取的 openid={}", openid);
        }
    }

    @RequestMapping("/saveAccessDetail")
    public WxInfo saveAccessDetail(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code) throws IOException {
        log.info("NikiController.saveAccessDetail。。。。。。。。。。。。。。。。。。。。。。。。。。。");

        String userAgent = request.getHeader("user-agent").toLowerCase();
        WxInfo wxInfo = new WxInfo();

        if (userAgent.indexOf("micromessenger") != -1) {
            log.info("==>用户访问的方式是微信");


            log.info("==> 先获取code,再获取openid 。code={}", code);
            Map params = new HashMap();
            params.put("appid", "wxb00b277049d87059");
            params.put("secret", "4f407849f4b50854ff6fbec3cc3d28a6");
            params.put("grant_type", "authorization_code");
            params.put("code", code);
            String result = HttpGetUtil.httpRequestToString(
                    "https://api.weixin.qq.com/sns/oauth2/access_token", params);
            if (result != null) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                String openid = jsonObject.get("openid").toString();
                log.info("==> 获取的 openid={}", openid);

                wxInfo.setCode(code);
                wxInfo.setOpenid(openid);
            }
        }
        // TODO 集成redis
        log.info("==>执行存入redis操作。。。。。。。。。。。");
        response.sendRedirect("https://www.apache.org/");
        log.info("==>跳转到最终实际访问的页面。。。。。。。。。。。。。。");
        return wxInfo;
    }

    // http://niki.nat300.top/share/middle
    @RequestMapping("/share/middle")
    public String middle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("NikiController.middle。。。。。。。。。。。。。。。。。。。。。。。。。。。");
        String userAgent = request.getHeader("user-agent").toLowerCase();
        String redirectUrl = "http://niki.nat300.top/saveAccessDetail";
        String encodeRedirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");
        log.info("。。。微信渠道URL编码前={}",redirectUrl);
        log.info("。。。微信渠道URL编码后={}",encodeRedirectUrl);
        if (userAgent.indexOf("micromessenger") != -1) {
            log.info("==>用户访问的方式是微信渠道");
            String wxRedirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?redirect_uri=" + encodeRedirectUrl + "&appid=wxb00b277049d87059&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
            log.info("==>重定向到微信的链接是{}" ,wxRedirectUrl);
             response.sendRedirect(wxRedirectUrl);
        } else {
            log.info("==>用户访问的方式是其他渠道");
            String otherRedirectUrl = redirectUrl + "?code=123";
            log.info("。。。。。。。。。。。其他渠道URL={}", otherRedirectUrl);
            response.sendRedirect(otherRedirectUrl);
        }
        return "";
    }

    @RequestMapping("/share/middle2")
    public String middle2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("NikiController.middle2。。。。。。。。。。。。。。。。。。。。。。。。。。。");
        String userAgent = request.getHeader("user-agent").toLowerCase();
        String redirectUrl = "http://niki.nat300.top/saveAccessDetail";
        String encodeRedirectUrl = URLEncoder.encode(redirectUrl + "&appid=wxb00b277049d87059&response_type=code&scope=snsapi_base&state=1#wechat_redirect", "UTF-8");
        log.info("。。。微信渠道URL编码前={}",redirectUrl + "&appid=wxb00b277049d87059&response_type=code&scope=snsapi_base&state=1#wechat_redirect");
        log.info("。。。微信渠道URL编码后={}",encodeRedirectUrl);
        if (userAgent.indexOf("micromessenger") != -1) {
            log.info("==>用户访问的方式是微信渠道");
            String wxRedirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?redirect_uri=" + encodeRedirectUrl;
            log.info("==>重定向到微信的链接是{}" ,wxRedirectUrl);
            response.sendRedirect(wxRedirectUrl);
        } else {
            log.info("==>用户访问的方式是其他渠道");
            String otherRedirectUrl = redirectUrl + "?code=123";
            log.info("。。。。。。。。。。。其他渠道URL={}", otherRedirectUrl);
            response.sendRedirect(otherRedirectUrl);
        }
        return "";
    }
//    @RequestMapping("/getOpenIdBySdk")
//    public void getOpenIdBySdk(HttpServletRequest request, HttpServletResponse response) throws WxErrorException {
//        log.info("==> 通过微信sdk方式。。。。。。。。。。。。。。。。。。。。。");
//        String url = request.getHeader("url");
//        WxMpService wxMpService = null;
//        WxOAuth2Service oAuth2Service = wxMpService.getOAuth2Service();
//        String s = oAuth2Service.buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_BASE, null);
//        String code = null;
//        wxMpService.getOAuth2Service().getAccessToken(code);
//    }


}
