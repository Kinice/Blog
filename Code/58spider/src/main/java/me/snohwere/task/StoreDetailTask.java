package me.snohwere.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jfinal.kit.StrKit;

import me.snohwere.model.Store;
import me.snohwere.queue.MyQueue;
import me.snohwere.util.Util;

/**
 * 商铺详情信息
 * @author STH
 * @date 2016年6月20日
 */
public class StoreDetailTask extends Task {
    private String area;

    private String place;
    private int source;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public StoreDetailTask(String url, String area, String place, int source) {
        super(url);
        this.area = area;
        this.place = place;
        this.source = source;
    }

    @Override
    public void process(Document doc) throws Exception {
        Store store = new Store();
        store.set("source", source);
        store.set("url", url);
        store.set("area", area);
        store.set("place", place);
        store.set("totalLook", getHtml(doc.getElementById("totalcount")));
        String date = getHtml(doc.getElementsByClass("other"));
        if (date.contains("发布时间")) {
            store.set("date", dateFormat.parse(date.split("<")[0].split("：")[1]));
        } else if (StrKit.notBlank(date)) {
            store.set("date", new Date());
        }
        store.set("title", getHtml(doc.select(".headline>h1")));
        store.set("content",
            getHtml(doc.getElementsByClass("maincon")).replaceAll("<[\\s\\S]*?>", "")
                .replaceAll("\n", "").replaceAll(" ", "").replaceAll("&nbsp;", ""));
        String phone = getHtml(doc.getElementById("t_phone"));
        if (phone.contains("img")) {
            phone = phone.split("'")[1];
        }
        store.set("phone", phone);
        Elements infoList = doc.select(".info>li");
        String otherInfo = "";
        String split = "：";
        for (Element element : infoList) {
            String info = element.html().replaceAll("<[\\s\\S]*>", "")
                .replaceAll("&nbsp;", "");
            if (info.contains(":")) {
                split = ":";
            }
            switch (info.split(split)[0]) {
            case "区域":
                break;
            case "租金":
                store.set("price", getHtml(element.getElementsByClass("redfont")));
                break;
            case "价格":
                store.set("price", getHtml(element.getElementsByClass("redfont")));
                break;
            case "地址":
                store.set("address", info.split(split)[1]);
                break;
            case "类型":
                store.set("type", info.split(split)[1]);
                break;
            case "面积":
                store.set("size", info.split(split)[1]);
                break;
            case "临近":
                store.set("near", info.split(split)[1]);
                break;
            case "历史经营":
                store.set("history", info.split(split)[1]);
                break;
            default:
                otherInfo += info + ",";
                break;
            }
        }
        store.set("otherInfo", otherInfo);
        MyQueue.DATA_QUEUE.put(store);
    }
}
