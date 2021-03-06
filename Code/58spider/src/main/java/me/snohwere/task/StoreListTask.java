package me.snohwere.task;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import me.snohwere.queue.MyQueue;

/**
 * 商铺列表
 * @author STH
 * @date 2016年6月20日
 */
public class StoreListTask extends Task {

    private static String URL = "http://bj.58.com/";

    private String code;
    //0-个人,1-商家
    private int source;
    
    private String area;

    private String place;

    private int page;

    public StoreListTask(String code, int page, int source, String area,
        String place) {
        super(URL + code + "/shangpucz/" + source + "/pn" + page);
        this.code = code;
        this.page = page;
        this.area = area;
        this.place = place;
        this.source = source;
    }

    @Override
    public void process(Document doc) throws Exception {
        Elements shops = doc.select("a.t");
        for (Element shop : shops) {
            String url = shop.attr("href").split("\\?")[0];
            MyQueue.TASK_QUEUE.put(new StoreDetailTask(url, area, place, source));
        }
        //如果有下一页
        Elements next = doc.getElementsByClass("next");
        if (next != null && !next.isEmpty()) {
            MyQueue.TASK_QUEUE
                .put(new StoreListTask(code, page + 1, source, area, place));
        }
    }
}
