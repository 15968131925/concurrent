package cn.itcast.test;

import cn.itcast.pattern.Downloader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "hh")
public class Test20 {
    public static void main(String[] args) {
        //线程1等待线程2的下载结果
        GuardedObject guardedObject = new GuardedObject();
        new Thread(()->{
            log.debug("等待结果");
            List<String> list = (List<String>) guardedObject.get();

            log.debug(list.toString());
        },"t1").start();

        new Thread(()->{
            log.debug("执行下载");
            List<String> download = null;
            try {
                download = Downloader.download();
            } catch (IOException e) {
                e.printStackTrace();
            }
            guardedObject.complete(download);

        },"t2").start();
    }

}


class GuardedObject {
    //结果
    private Object response;

    //获取结果
    public Object get() {
        synchronized (this) {
            //没有结果
            while (response == null) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }
    }

    //产生结果
    public void complete(Object response) {
        synchronized (this) {
            //给结果成员变量赋值
            this.response = response;
            this.notifyAll();
        }
    }
}