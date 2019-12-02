package com.maple.agent;

import com.sun.tools.attach.VirtualMachine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * 文件变更监听器
 *
 * @author lfy
 * @date 2019/11/28 16:50
 **/
@Slf4j
public class MapleHotLoadWatchDog {

	/**
	 * 代理jar包
	 */
	private static final String AGENT_JAR_NAME = "maple-agent-4.0.0.jar";

	/**
	 * 代理jar包路径
	 */
	private String agentJarPath;

	/**
	 * 启动热更
	 *
	 * @param libPath     热更包路径
	 * @param hotLoadPath 监听的目录路径
	 */
	public static void start(String libPath, String hotLoadPath) throws Exception {
		File jarFile = new File(libPath + File.separator + AGENT_JAR_NAME);
		if (!jarFile.exists()) {
			log.error("未找到热更包，热更未启动");
			return;
		}
		// start watch dog
		MapleHotLoadWatchDog mapleHotLoadWatchDog = new MapleHotLoadWatchDog();
		mapleHotLoadWatchDog.agentJarPath = jarFile.getAbsolutePath();
		mapleHotLoadWatchDog.startWatch(hotLoadPath);
	}

	/**
	 * 启动热更脚本目录监听
	 *
	 * @param hotLoadPath 监听的目录路径
	 */
	private void startWatch(String hotLoadPath) throws Exception {
		File file = new File(hotLoadPath);
		if (!file.exists()) {
			throw new RuntimeException("文件夹不存在！ " + hotLoadPath);
		}
		//watch file
		FileAlterationObserver observer = new FileAlterationObserver(file);
		observer.addListener(new FileAlterationListenerAdaptor() {
			@Override
			public void onFileCreate(File file) {
				log.info("file:" + file.getName() + " onCreate");
				if (!file.getName().endsWith(".class")) {
					return;
				}
				updateClass(file);
			}

			@Override
			public void onFileChange(File file) {
				log.info("file:" + file.getName() + " onChange");
				if (!file.getName().endsWith(".class")) {
					return;
				}
				updateClass(file);
			}

			@Override
			public void onFileDelete(File file) {
				log.info("file:" + file.getName() + " onDelete");
			}
		});

		FileAlterationMonitor fileAlterationMonitor = new FileAlterationMonitor(3000);
		fileAlterationMonitor.addObserver(observer);
		fileAlterationMonitor.start();
		log.info("start watch dog " + file.getAbsolutePath());
	}

	/**
	 * 热更目标Class
	 *
	 * @param file 目标class文件
	 */
	private void updateClass(File file) {
		try {
			// runtime vm process id
			String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
			VirtualMachine runtimeVm = VirtualMachine.attach(pid);
			String path = file.getAbsolutePath();
			runtimeVm.loadAgent(agentJarPath, path);
		} catch (Throwable e) {
			log.error("热更Class文件失败 " + file.getName(),e);
		}
	}

}
