package com.perfma.xpocket.plugin.jdb;

import com.perfma.xlab.xpocket.spi.AbstractXPocketPlugin;
import com.perfma.xlab.xpocket.spi.context.SessionContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.regex.Pattern;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class JDBPlugin extends AbstractXPocketPlugin implements Runnable {

    private static final String LOGO = "      _   ____    ____  \n"
                                     + "     | | |  _ \\  | __ ) \n"
                                     + "  _  | | | | | | |  _ \\ \n"
                                     + " | |_| | | |_| | | |_) |\n"
                                     + "  \\___/  |____/  |____/ \n";

    private Process jdbProc;

    public static final String lineSeparator = System.getProperty("line.separator");

    private LinkedBlockingQueue<XPocketProcess> processes = new LinkedBlockingQueue<>();

    private Pattern pattern = Pattern.compile("[\\S]*\\[[0-9]{1,}\\]");

    public void invoke(XPocketProcess process) {
        try {
            String command = process.getCmd();
            processes.add(process);
            switch (command) {
                case "jdb":
                    jdbProc = Runtime.getRuntime()
                            .exec(handleCmdStr(process.getCmd(),
                                    process.getArgs()));
                    Thread t = new Thread(this);
                    t.start();
                    break;
                default:
                    jdbProc.getOutputStream().write(handleCmd(process.getCmd(),
                            process.getArgs()));
                    jdbProc.getOutputStream().flush();
            }

        } catch (Throwable ex) {
            processes.remove(process);
            process.output(ex.getMessage());
            process.end();
        }
    }

    public boolean isAvaibleNow(String cmd) {
        if (jdbProc == null || !jdbProc.isAlive()) {
            return "jdb".equals(cmd);
        } else {
            return !"jdb".equals(cmd);
        }
    }

    @Override
    public void switchOn(SessionContext context) {
        super.switchOn(context); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void destory() throws Throwable {
        jdbProc.getOutputStream().write("quit".getBytes());
        jdbProc.getOutputStream().flush();
    }

    @Override
    public void run() {

        try {
            InputStream instr = jdbProc.getInputStream();
            XPocketProcess process = processes.take();
            try {
                int ret_read = 0, index = 0;
                byte[] line = new byte[1024];

                LOOP:
                for (;;) {
                    ret_read = instr.read();
                    if (ret_read == -1) {
                        break;
                    }

                    if (process == null) {
                        process = processes.take();
                    }

                    switch (ret_read) {
                        case '\r':
                        case '\n':
                            String lineStr = new String(line, 0, index);
                            if (!lineStr.trim().equalsIgnoreCase(process.getCmd())) {
                                process.output(lineStr + lineSeparator);
                            }
                            index = 0;
                            break;
                        case '>':
                            line[index++] = (byte) ret_read;
                            LockSupport.parkNanos(TimeUnit.NANOSECONDS.convert(100, TimeUnit.MILLISECONDS));
                            String flag = new String(line, 0, index);
                            if (flag.endsWith(">") && index < 3) {
                                process.end();
                                process = null;
                                index = 0;
                            }
                            break;
                        case ']':
                            line[index++] = (byte) ret_read;
                            flag = new String(line, 0, index);
                            LockSupport.parkNanos(TimeUnit.NANOSECONDS.convert(100, TimeUnit.MILLISECONDS));
                            if (pattern.matcher(flag).matches()) {
                                process.end();
                                process = null;
                                index = 0;
                            }
                            break;
                        default:
                            line[index++] = (byte) ret_read;
                    }

                }
                if (process != null) {
                    process.end();
                }
            } catch (IOException e) {
                process.output("Exception while reading socket:" + e.getMessage());
                process.end();
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

    }

    private byte[] handleCmd(String cmd, String[] args) {
        return handleCmdStr(cmd, args).getBytes();
    }

    private String handleCmdStr(String cmd, String[] args) {
        StringBuilder cmdStr = new StringBuilder(cmd);

        if (args != null) {
            for (String arg : args) {
                cmdStr.append(' ').append(arg);
            }
        }

        cmdStr.append("\n");

        return cmdStr.toString();
    }

    @Override
    public void printLogo(XPocketProcess process) {
        process.output(LOGO);
    }

}
