package com.oneym.libslab.bean;

/**
 * 腾讯QQ返回的用户基本信息
 *
 * @author oneym
 * @since 20151223142554
 * @deprecated 201512
 */
public class QQBean {
    private int is_yellow_year_vip = 0;
    //返回码,具体参考http://wiki.open.qq.com/wiki/mobile/%E5%85%AC%E5%85%B1%E8%BF%94%E5%9B%9E%E7%A0%81%E8%AF%B4%E6%98%8E
    private int ret = 0;
    //QQ头像40*40
    private String figureurl_qq_1 = "";
    //QQ头像100*100
    private String figureurl_qq_2 = "";
    //QQ昵称
    private String nickname ="";
    private int yellow_vip_level = 0;
    private int is_lost = 0;
    private String msg = "";
    //QQ上的所在城市/区
    private String city = "";
    private String figureurl_1 = "";
    private int vip = 0;
    private int level = 0;
    private String figureurl_2 = "";
    //QQ上的所在省份/直辖市
    private String province = "";
    private int is_yellow_vip = 0;
    //QQ性别
    private String gender = "";
    private String figureurl = "";

    public int getIs_yellow_year_vip() {
        return is_yellow_year_vip;
    }

    public void setIs_yellow_year_vip(int is_yellow_year_vip) {
        this.is_yellow_year_vip = is_yellow_year_vip;
    }

    /**
     * 返回码,具体参考http://wiki.open.qq.com/wiki/mobile/%E5%85%AC%E5%85%B1%E8%BF%94%E5%9B%9E%E7%A0%81%E8%AF%B4%E6%98%8E
     */
    public int getRet() {
        return ret;
    }

    /**
     * 返回码,具体参考http://wiki.open.qq.com/wiki/mobile/%E5%85%AC%E5%85%B1%E8%BF%94%E5%9B%9E%E7%A0%81%E8%AF%B4%E6%98%8E
     */
    public void setRet(int ret) {
        this.ret = ret;
    }

    /**
     * QQ头像40*40
     */
    public String getFigureurl_qq_1() {
        return figureurl_qq_1;
    }

    /**
     * QQ头像40*40
     */
    public void setFigureurl_qq_1(String figureurl_qq_1) {
        this.figureurl_qq_1 = figureurl_qq_1;
    }

    /**
     * QQ头像100*100
     */
    public String getFigureurl_qq_2() {
        return figureurl_qq_2;
    }

    /**
     * QQ头像100*100
     */
    public void setFigureurl_qq_2(String figureurl_qq_2) {
        this.figureurl_qq_2 = figureurl_qq_2;
    }

    /**
     * QQ昵称
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * QQ昵称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getYellow_vip_level() {
        return yellow_vip_level;
    }

    public void setYellow_vip_level(int yellow_vip_level) {
        this.yellow_vip_level = yellow_vip_level;
    }

    public int getIs_lost() {
        return is_lost;
    }

    public void setIs_lost(int is_lost) {
        this.is_lost = is_lost;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * QQ上的所在城市/区
     */
    public String getCity() {
        return city;
    }

    /**
     * QQ上的所在城市/区
     */
    public void setCity(String city) {
        this.city = city;
    }

    public String getFigureurl_1() {
        return figureurl_1;
    }

    public void setFigureurl_1(String figureurl_1) {
        this.figureurl_1 = figureurl_1;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getFigureurl_2() {
        return figureurl_2;
    }

    public void setFigureurl_2(String figureurl_2) {
        this.figureurl_2 = figureurl_2;
    }

    /**
     * QQ上的所在省份/直辖市
     */
    public String getProvince() {
        return province;
    }

    /**
     * QQ上的所在省份/直辖市
     */
    public void setProvince(String province) {
        this.province = province;
    }

    public int getIs_yellow_vip() {
        return is_yellow_vip;
    }

    public void setIs_yellow_vip(int is_yellow_vip) {
        this.is_yellow_vip = is_yellow_vip;
    }

    /**
     * QQ性别
     */
    public String getGender() {
        return gender;
    }

    /**
     * QQ性别
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFigureurl() {
        return figureurl;
    }

    public void setFigureurl(String figureurl) {
        this.figureurl = figureurl;
    }
}
