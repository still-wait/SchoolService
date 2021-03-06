package com.practice.controller;

import com.practice.bean.*;
import com.practice.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by Y-GH on 2017/5/15.
 */
@RestController
@RequestMapping("/wanqing")
public class LoginController {
    //    注入UserDao
    @Autowired
    private UserDao userDao;
    private String URL = "http://10.138.68.124";

    /**
     * 登录
     * @param userid
     * @param userpassword
     * @return
     */
    @RequestMapping(value="/login", method= RequestMethod.POST)
    public Userbean findUsersByNameAndPsd(@RequestParam(value = "userid", required = true) String userid,
                                          @RequestParam(value = "password", required = true) String userpassword) {
        System.out.println("开始登录===>>"+userid+"--->>"+userpassword);
        Userbean user = userDao.getLogin(userid,userpassword);
        System.out.println("===>>"+user.getUsername());
        System.out.println("===>>登录完成");
        return user;
    }

    /**
     * 注册
     * @param userid
     * @param userpassword
     * @param username
     * @param sex
     * @param xueyuan
     * @param zhuanye
     * @param type
     * @return
     */
    @RequestMapping(value="/resign", method= RequestMethod.POST)
    public Flag insert(@RequestParam(value = "userid", required = true) String userid,
                           @RequestParam(value = "password", required = true) String userpassword,
                           @RequestParam(value = "username", required = true) String username,
                           @RequestParam(value = "sex", required = true) String sex,
                           @RequestParam(value = "xueyuan", required = true) String xueyuan,
                           @RequestParam(value = "zhuanye", required = true) String zhuanye,
                           @RequestParam(value = "type", required = true) String type) {
        System.out.println("开始注册===>>"+userid+"--->>"+userpassword);
        int flag = userDao.resign(userid,userpassword,username,sex,xueyuan,zhuanye,type);
        System.out.println("===>>"+flag);
        Flag f1=new Flag();
        if(flag>0){
            System.out.println("===>>注册完成");
            f1.setStatus("success");
        }else {
            System.out.println("===>>注册失败");
            f1.setStatus("failed");
        }
        return f1;
    }

    public String currentTime() {
        Date nowTime = new Date(System.currentTimeMillis());
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        Random rand = new Random();
        int randNum = rand.nextInt(1000);
        retStrFormatNowDate = retStrFormatNowDate + randNum;
        return retStrFormatNowDate;
    }

    public String currentTime2() {
        Date nowTime = new Date(System.currentTimeMillis());
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyyMMdd");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;
    }


    /**
     * 上传校园美景
     * @param request
     * @param response
     * @param file
     * @param userid
     * @param imgdesc
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @RequestMapping(value="/updateUserImage",method=RequestMethod.POST)
    @ResponseBody
    public int updateUserImage(HttpServletRequest request, HttpServletResponse response, @RequestParam("file_img") MultipartFile file,
                               @RequestParam("userid") String userid,
                               @RequestParam("imgdesc") String imgdesc) throws IllegalStateException, IOException {
        System.out.println("开始上传校园美景图片");
        String UserImage=null;
        String filename=null;
        if(!file.isEmpty()) {
            //上传文件路径
            System.out.println("开始写入文件");
            String path = request.getSession().getServletContext().getRealPath("upload");
            //上传文件名
            filename = currentTime()+".jpg";
            File filepath = new File(path,filename);
            //判断路径是否存在，如果不存在就创建一个
            if (!filepath.getParentFile().exists()) {
                filepath.getParentFile().mkdirs();
            }
            //将上传文件保存到一个目标文件当中
            UserImage=path + File.separator + filename;
            System.out.println("UserImage:"+UserImage);
            file.transferTo(new File(UserImage));
            System.out.println("写入成功");
        } else {
            System.out.println("写入失败");
        }
        int updateImage=0;
        updateImage=userDao.setIMG(userid,currentTime(),filename,imgdesc,currentTime2());
        System.out.println("updateImage:"+updateImage);
        return updateImage;
    }


    /**
     * 获取首页列表
     * @param page
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/getimg/{page}",method=RequestMethod.GET)
    @ResponseBody
    public List<PictureBean> findUserImage(@PathVariable String page,HttpServletRequest request,HttpServletResponse response){
        FileInputStream fis = null;
        OutputStream os = null;
        System.out.println("====开始获取首页图片列表=====》");
//        System.out.println(page);
        List<PictureBean> list= userDao.getimg();

        for(int i = 0;i<list.size();i++){
            String imggpath=list.get(i).getImgpath();

//            findImage(page,imggpath,request,response);

            list.get(i).setUrl(URL+":8080/wanqing/getimg/"+page+"/"+imggpath);

        }

        System.out.println("===>>获取成功");
        System.out.println(list);
        return list;
    }


    /**
     * 图片加载
     * @param page
     * @param imgpath
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/getimg/{page}/{imgpath}",method=RequestMethod.GET)
    @ResponseBody
    public String findImage(@PathVariable String page,@PathVariable String imgpath,HttpServletRequest request,HttpServletResponse response){
        FileInputStream fs = null;
        OutputStream os = null;
//        System.out.println("===>>登录完成");
//        System.out.println(page);

        String imggpath="E:\\Graduation_Help\\SchoolServiceWQ\\src\\main\\webapp\\upload\\"+imgpath+".jpg";
        try{
            fs=new FileInputStream(imggpath);
            os = response.getOutputStream();
            int count = 0;
            byte[] buffer = new byte[1024 * 8];
            while ((count = fs.read(buffer)) != -1) {
                os.write(buffer, 0, count);
                os.flush();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return "ok";
    }


    @RequestMapping(value="/gethead/{userid}",method=RequestMethod.GET)
    @ResponseBody
    public String findheadImage(@PathVariable String userid,HttpServletRequest request,HttpServletResponse response){
        FileInputStream fs = null;
//        OutputStream os = null;
        System.out.println("===>>加载用户头像");
        System.out.println(userid);
        String imgpath = userDao.getHeadimg(userid);
        String imggpath="E:\\Graduation_Help\\SchoolServiceWQ\\src\\main\\webapp\\upload\\"+imgpath;

        try{
            fs=new FileInputStream(imggpath);
            OutputStream os = response.getOutputStream();
            int count = 0;
            byte[] buffer = new byte[1024 * 8];
            while ((count = fs.read(buffer)) != -1) {
                os.write(buffer, 0, count);
                os.flush();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return "ok";
    }


    @RequestMapping(value="/updateheadimg",method=RequestMethod.POST)
    @ResponseBody
    public int updateHeadImage(HttpServletRequest request, HttpServletResponse response, @RequestParam("file_img") MultipartFile file,
                               @RequestParam("userid") String userid) throws IllegalStateException, IOException {
        System.out.println("开始更新用户头像");
        String UserImage=null;
        String filename=null;
        if(!file.isEmpty()) {
            //上传文件路径
            System.out.println("开始写入");
            String path = request.getSession().getServletContext().getRealPath("upload");
            //上传文件名
            filename = currentTime()+".jpg";
            File filepath = new File(path,filename);
            //判断路径是否存在，如果不存在就创建一个
            if (!filepath.getParentFile().exists()) {

                filepath.getParentFile().mkdirs();
            }else {}
            //将上传文件保存到一个目标文件当中
            UserImage=path + File.separator + filename;
            System.out.println("UserImage:"+UserImage);
            file.transferTo(new File(UserImage));
            System.out.println("保存成功");
        } else {
            System.out.println("保存失败");
        }
        int updateImage=0;
        updateImage=userDao.setHeadimg(userid,filename);
        System.out.println("updateImage:"+updateImage);
        return updateImage;
    }


    @RequestMapping(value="/deleteimg",method=RequestMethod.POST)
    @ResponseBody
    public Flag updateHeadImage(@RequestParam(value = "imgid", required = true) String imgid) {
        System.out.println("开始删除校园美景图片");

        int flag = userDao.DeleteSchoolPic(imgid);
        Flag f1=new Flag();
        if(flag>0){
            System.out.println("删除成功");
            f1.setStatus("success");
        }else {
            System.out.println("删除失败");
            f1.setStatus("failed");
        }
        return f1;
    }


    /**
     * 课程表
     * @param userid
     * @return
     */
    @RequestMapping(value="/getweeklin", method= RequestMethod.POST)
    public List<WeeklinBean> getWeeklin(@RequestParam(value = "userid", required = true) String userid) {
        System.out.println("开始获取课程表");
        System.out.println("===>>"+userid+"--->>");
        List<WeeklinBean> list = userDao.getWeeklin(userid);
        System.out.println(list);
        return list;
    }



    /**
     * 学校列表
     * @param userid
     * @return
     */
    @RequestMapping(value="/schoollist", method= RequestMethod.POST)
    public List<SchoolBean> schoolSelect(@RequestParam(value = "userid", required = true) String userid) {
        System.out.println("=====》开始获取学校信息");
        List<SchoolBean> list = userDao.getSchool();
        System.out.println(list);
        return list;
    }

    /**
     * 列表
     * @param userid
     * @return
     */
    @RequestMapping(value="/schoolnewslist", method= RequestMethod.POST)
    public List<SchoolNewsBean> schoolnewsSelect(@RequestParam(value = "userid", required = true) String userid) {
        System.out.println("====》开始获取校园新闻");
        List<SchoolNewsBean> list = userDao.getNews();
        System.out.println(list);
        return list;
    }

    /**
     * 列表
     * @param userid
     * @return
     */
    @RequestMapping(value="/schoolnoticeslist", method= RequestMethod.POST)
    public List<SchoolNoticesBean> schoolnoticeSelect(@RequestParam(value = "userid", required = true) String userid) {
        System.out.println("====》开始获取校园通知");
        List<SchoolNoticesBean> list = userDao.getNotices();
        System.out.println(list);
        return list;
    }

    /**
     * 列表
     * @param userid
     * @return
     */
    @RequestMapping(value="/storemsglist", method= RequestMethod.POST)
    public List<StoreBean> storemsgSelect(@RequestParam(value = "userid", required = true) String userid) {
        System.out.println("====》开始获取周边商店");
        List<StoreBean> list = userDao.getStore();
        System.out.println(list);
        return list;
    }

    /**
     * 列表
     * @param userid
     * @return
     */
    @RequestMapping(value="/playgroundlist", method= RequestMethod.POST)
    public List<PlaygroundBean> PlaygroundSelect(@RequestParam(value = "userid", required = true) String userid) {
        System.out.println("====》开始获取周边娱乐场所");
        List<PlaygroundBean> list = userDao.getPlayground();
        System.out.println(list);
        return list;
    }


}
