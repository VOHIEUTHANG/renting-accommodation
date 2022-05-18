package ptithcm.controller;
import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;

import ptithcm.dao.BaiVietDao;
import ptithcm.dao.NguoiDungDao;
import ptithcm.entity.BaiVietEntity;
import ptithcm.entity.TaiKhoanEntity;
import ptithcm.service.UserService;
@Controller
@RequestMapping("/nguoidung")
public class NguoiDungController {
    @Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	UserService userService ;
	@RequestMapping(value = "/dangky",method= RequestMethod.GET)
	public String home () {
		return "user/signup-page";
	}
	@RequestMapping(value = "/dangki", method = RequestMethod.POST)
	public String dangki (
	@RequestParam("username") String username,
	@RequestParam("pass") String pass,
	@RequestParam("confirmpass") String confirm,
	@RequestParam("email") String email,
	ModelMap model) {
		if (pass.equals(confirm)&&!username.isEmpty()&&!pass.isEmpty()) {
			TaiKhoanEntity tk =new TaiKhoanEntity(username,passwordEncoder.encode(pass),true);
			NguoiDungDao nguoiDungDao = new NguoiDungDao();
			Integer temp = nguoiDungDao.insertUser(tk);
			if(temp == 2) {
				model.addAttribute("mes","Tên đăng nhập đã bị trùng");
			}
			if(temp == 1 )
			{ 
				model.addAttribute("mes","Đăng kí thành công");
		    }
			else 
			{
				model.addAttribute("mes","Có lỗi khi đăng kí");
			}
		}
		return "Posts/trangchu";
	}

	@RequestMapping("/dangbai")
	public String dangbaiget(ModelMap model)
	{
		NguoiDungDao userDao = new NguoiDungDao();
		String username= userService.currentUserName();
		TaiKhoanEntity tk= userDao.findByUserName(username);
		model.addAttribute("user",tk.getNguoidung());
		return "Posts/DangBai";
	}
    @RequestMapping(value = "/addnew", method = RequestMethod.GET)
	public @ResponseBody String dangbai (HttpServletRequest request)
	{
		String TieuDe= request.getParameter("TieuDe");
		String MoTa= request.getParameter("MoTa");
		String SDT= request.getParameter("SDT");
		String Gia= request.getParameter("Gia");
		String DienTich= request.getParameter("DienTich");
		String Tinh= request.getParameter("Tinh");
		String Huyen= request.getParameter("Huyen");
		String Xa= request.getParameter("Xa");
		System.out.print(TieuDe);
		return Tinh;
	}

    @RequestMapping("/index")
	public String QuanLiBaiViet(ModelMap model,HttpServletRequest request){
	  NguoiDungDao userDao = new NguoiDungDao();
	  String username= userService.currentUserName();
	  List<BaiVietEntity> bviet = userDao.getAllBaiviet(username);
	  int page= ServletRequestUtils.getIntParameter( request,"p",0);
        PagedListHolder pagedListHolder =new PagedListHolder(bviet);
        pagedListHolder.setPage(page);
        pagedListHolder.setPage(page);
		pagedListHolder.setMaxLinkedPages(5);
		pagedListHolder.setPageSize(5);
	  model.addAttribute("BaiViet",pagedListHolder);
	  model.addAttribute("UserName",username);
      return "user/quanlybaidang";
	}

	@RequestMapping("/trangcanhan")
	public String trangcanhan (ModelMap model){
	NguoiDungDao userDao = new NguoiDungDao();
	String username= userService.currentUserName();
	TaiKhoanEntity tk= userDao.findByUserName(username);

	model.addAttribute("user",tk.getNguoidung());
	model.addAttribute("acc",tk);
	List<BaiVietEntity> bviet= (List<BaiVietEntity>) tk.getNguoidung().getBaiviet();
	int tatca=0;
	int chuaduyet=0;
	int danghienthi=0;
	int hethan=0;
	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	for(BaiVietEntity i: bviet){
		tatca++;
		if(i.getTinhtrang()==true && i.getAn()==false && i.getChitietbaiviet().getThoigianketthuc().compareTo(timestamp)<=0) danghienthi++;
		if(!i.getTinhtrang()) chuaduyet++;
		if(i.getChitietbaiviet().getThoigianketthuc().compareTo(timestamp)>0) hethan++;

	}

	model.addAttribute("tatca",tatca);
	model.addAttribute("postList",bviet);
	model.addAttribute("chuaduyet",chuaduyet);
	model.addAttribute("danghienthi",danghienthi);
	model.addAttribute("hethan",hethan);
       return "user/trangcanhan";
	}

	@RequestMapping(value = "/setan",method=RequestMethod.GET)
	public @ResponseBody String SetAn(HttpServletRequest request){
		Long mabaiviet= Long.parseLong(request.getParameter("mabaiviet"));
	    System.out.println(mabaiviet);
		NguoiDungDao userDao = new NguoiDungDao();
		String username= userService.currentUserName();
		TaiKhoanEntity tk= userDao.findByUserName(username);
		BaiVietDao bv= new BaiVietDao();
		BaiVietEntity bv1= bv.getById(mabaiviet).get(0);
        if(tk.getNguoidung().getMaND()== bv1.getNguoidung().getMaND())
		   if(bv.SetAn(bv1)) 
	         return "1";
		return "0";	
	}
}
