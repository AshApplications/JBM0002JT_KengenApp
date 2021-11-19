package com.water.alkaline.kengen.model.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

@SerializedName("categorys")
@Expose
private List<Category> categorys = null;
@SerializedName("subcategorys")
@Expose
private List<Subcategory> subcategorys = null;
@SerializedName("channels")
@Expose
private List<Channel> channels = null;
@SerializedName("pdfs")
@Expose
private List<Pdf> pdfs = null;
@SerializedName("banners")
@Expose
private List<Banner> banners = null;

public List<Category> getCategorys() {
return categorys;
}

public void setCategorys(List<Category> categorys) {
this.categorys = categorys;
}

public List<Subcategory> getSubcategorys() {
return subcategorys;
}

public void setSubcategorys(List<Subcategory> subcategorys) {
this.subcategorys = subcategorys;
}

public List<Channel> getChannels() {
return channels;
}

public void setChannels(List<Channel> channels) {
this.channels = channels;
}

public List<Pdf> getPdfs() {
return pdfs;
}

public void setPdfs(List<Pdf> pdfs) {
this.pdfs = pdfs;
}

public List<Banner> getBanners() {
return banners;
}

public void setBanners(List<Banner> banners) {
this.banners = banners;
}

}