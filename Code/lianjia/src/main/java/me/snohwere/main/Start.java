package me.snohwere.main;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;

import me.snohwere.model.Area;
import me.snohwere.model.Block;
import me.snohwere.queue.MyQueue;
import me.snohwere.spider.DataThread;
import me.snohwere.spider.Spider;
import me.snohwere.task.AreaTask;
import me.snohwere.task.PlaceTask;

public class Start {

    public static void main(String args[]) {
        // 数据库连接
        C3p0Plugin c3p0Plugin = new C3p0Plugin(
            "jdbc:mysql://127.0.0.1/lianjia?useUnicode=true", "root", "qwer");
        c3p0Plugin.start();
        ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
        arp.addMapping("block", Block.class);
        arp.addMapping("area", Area.class);
        arp.start();
        letUsGo();
    }

    private static void letUsGo() {
        MyQueue.TASK_QUEUE.add(new PlaceTask("dongcheng"));
        System.out.println("Let's go");
        // HTTP线程
        for (int i = 1; i < 2; i++) {
            Spider spider = new Spider(i);
            spider.start();
        }
       /* // 数据库线程
        DataThread spider = new DataThread(1);
        spider.start();*/
    }

}
