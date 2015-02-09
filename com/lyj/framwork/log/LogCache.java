package com.lyj.framwork.log;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.text.TextUtils;
import android.util.Log;

import com.lyj.framwork.log.util.MemoryStatus;

/**
 * [Log缓存]<BR>
 * 
 * @author 周昕
 * @version [EasierBaseLine_Android, 2013-1-28]
 */
public class LogCache {
	/**
	 * 日志标记
	 */
	public static final String TAG = "LogCache";

	/**
	 * 原因：存储卡剩余空间大于50M，规格要求日志�?占用不超�??M，最多缓�??个文件s
	 */
	public static final int FILE_AMOUNT = 3;

	/**
	 * file size limitation per log file
	 */
	public static final long MAXSIZE_PERFILE = 1048576;

	/**
	 * SD卡可存储的最小空间要求：50M
	 */
	private static final long MIX_SIZE = 5242880;

	/**
	 * instance of LogCache
	 */
	private static final LogCache INSTANCE = new LogCache();

	/**
	 * 时间格式
	 */
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:SS.sss");

	private final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

	private volatile boolean started;

	private volatile Thread logWorkerThread;

	private LogWriter logWriter = null;

	private int counter = 0;

	/**
	 * 构�?�??
	 */
	private LogCache() {
		this(Logger.LOG_FILE_PATH, FILE_AMOUNT, MAXSIZE_PERFILE);
	}

	private LogCache(String filePath) {
		this(filePath, 0, 0);
	}

	private LogCache(String filePath, int fileAmount, long maxSize) {
		this.logWriter = new LogWriter(new File(filePath), fileAmount, maxSize);
	}

	/**
	 * [�??��话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @return
	 */
	static LogCache getInstance() {
		return INSTANCE;
	}

	/**
	 * put log info into the synchronized queue
	 * 
	 * @param msg
	 *            msg
	 */
	public void write(String msg) {
		if (started) {
			try {
				queue.put(msg);
			} 
			catch (InterruptedException e) {
				Log.e("LogCache", "", e);
			}
		}

	}

	/**
	 * construct log info into the queue
	 * 
	 * @param level
	 *            level
	 * @param tag
	 *            tag
	 * @param msg
	 *            msg
	 * @param id
	 *            id
	 * @param methodName
	 *            methodName
	 */
	public void write(String level, String tag, String msg, long id,
			String methodName) {
		StringBuilder sbr = new StringBuilder();
		// 时间
		sbr.append('[');
		sbr = addCurrentTime(sbr);
		sbr.append(']');
		// 进程和线�??
		sbr.append('[');
		sbr.append("P:").append(android.os.Process.myPid()).append('/')
				.append("T:").append(id);
		sbr.append(']');
		// 函数名，�??��文件和行�??
		sbr.append('[');
		sbr.append(tag).append(' ').append(methodName);
		sbr.append(']');
		// [Enter 或�? Leave]||[Err 或�? War 或�? Inf]
		sbr.append('[');
		sbr.append(level);
		sbr.append(']');
		// 消息非空的时候输�??
		if (!TextUtils.isEmpty(msg)) {
			sbr.append('\n').append(msg);
		}

		write(sbr.toString());
	}

	/**
	 * 写入�??��时间的日�??
	 */
	public void writeBegin() {

		StringBuilder sbr = new StringBuilder();

		// 输入起始内容
		sbr.append("Begin Time:");
		sbr = addCurrentTime(sbr);

		write(sbr.toString());
	}

	/**
	 * 添加时间
	 * 
	 * @param sbr
	 *            StringBuilder待添�??
	 * @return 添加过后的StringBuilder
	 */
	public StringBuilder addCurrentTime(StringBuilder sbr) {
        if (null != sbr) {
            // 添加时间,格式:YYYY-MM-DD HH-MM-SS.mmm
            sbr.append(TIME_FORMAT.format(System.currentTimeMillis()));
        }

		return sbr;
	}

	/**
	 * judge whether the external memory writable or not
	 * 
	 * @param text
	 *            text
	 * @return isExternalMemoryAvailable
	 */
	public boolean isExternalMemoryAvailable(String text) {
		// 无存储卡，不记录日志
		// 存储卡剩余空间�? 50M，不记录日志�??
		// 存储卡剩余空�??=50M，日志文件�?占用空间不超�??M�??

		return MemoryStatus.isExternalMemoryAvailable(text.getBytes().length
				+ MIX_SIZE);
	}

	/**
	 * [�??��话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @return CacheSize
	 */
	public synchronized long getCacheSize() {
		long size = 0;
		for (String text : queue) {
			size += text.getBytes().length;
		}
		return size;
	}

	public boolean isStarted() {
		return started;
	}

	public boolean isLogThreadNull() {
		return null == logWorkerThread;
	}

	/**
	 * [�??��话功能简述]<BR>
	 * [功能详细描述]
	 */
	public synchronized void start() {
		if (null == logWorkerThread) {
			logWorkerThread = new Thread(new LogTask(), "Log Worker Thread - "
					+ counter);
		}
		if (started || !logWriter.initialize()) {
			return;
		}
		Log.v(TAG, "Log Cache instance is starting ...");
		started = true;
		logWorkerThread.start();
		Log.v("LogCache", "Log Cache instance is started");
	}

	/**
	 * [�??��话功能简述]<BR>
	 * [功能详细描述]
	 */
	public synchronized void stop() {
		Log.v("LogCache", "Log Cache instance is stopping...");
		started = false;
		queue.clear();
		logWriter.close();
		if (null != logWorkerThread) {
			logWorkerThread.interrupt();
		}
		logWorkerThread = null;
		Log.v("LogCache", "Log Cache instance is stopped");
	}

	/**
	 * [写入日志任务]<BR>
	 * [功能详细描述]
	 * 
	 * @author 周昕
	 * @version [EasierBaseLine_Android, 2013-1-28]
	 */
	private final class LogTask implements Runnable {

		public LogTask() {
			counter++;
		}

		private void dealMsg() throws InterruptedException {
			String msg = null;
			while (started && !Thread.currentThread().isInterrupted()) {
				msg = queue.take();
				synchronized (logWriter) {
					// Log.v(TAG,
					// "AvailableExternalMemorySize:"
					// + MemoryStatus.getAvailableExternalMemorySize());
					if (isExternalMemoryAvailable(msg)) {
						// if current file is deleted, rebuild it
						if (!logWriter.isCurrentExist()) {
							Log.v(TAG, "current is initialing...");
							if (!logWriter.initialize()) {
								continue;
							}
						}
						// if current log file reaches size limitation, log into
						// next log file
						else if (!logWriter.isCurrentAvailable()) {
							Log.v(TAG, "current is rotating...");
							if (!logWriter.rotate()) {
								continue;
							}
						}
						logWriter.println(msg);
					} 
					else if (logWriter.clearSpace()) {
						if (!logWriter.rotate()) {
							continue;
						}
						logWriter.println(msg);
					} 
					else {
						Log.e(TAG, "can't log into sdcard.");
					}
				}
			}
		}

		public void run() {
			try {
				dealMsg();
			}
			catch (InterruptedException e) {
				Log.e(TAG, Thread.currentThread().toString(), e);
			}
            catch (RuntimeException e) {
                Log.e(TAG,
                    Thread.currentThread().toString(),
                    e);
                logWorkerThread = new Thread(new LogTask(),
                    "Log Worker Thread - " + counter);
                started = false;
            }
			finally {
				Log.v(TAG, "Log Worker Thread is terminated.");
			}
		}

	}

}
