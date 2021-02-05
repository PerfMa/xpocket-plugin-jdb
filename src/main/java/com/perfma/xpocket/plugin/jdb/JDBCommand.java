package com.perfma.xpocket.plugin.jdb;

import com.perfma.xlab.xpocket.spi.XPocketPlugin;
import com.perfma.xlab.xpocket.spi.command.AbstractXPocketCommand;
import com.perfma.xlab.xpocket.spi.command.CommandList;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
@CommandList(names={"jdb","connectors","run","threads","thread","suspend","resume",
    "where","wherei","up","down","kill","interrupt","print","dump","eval","set",
    "locals","classes","class","methods","fields","threadgroups","threadgroup",
    "clear","catch","ignore","step","stepi","list","use","exclude","classpath",
    "lock","threadlocks","pop","reenter","redefine","disablegc","enablegc","!!",
    "version","stop","next","cont"},
        usage={"jdb <options> <class> <arguments>",
            "列出此 VM 中可用的连接器和传输",
            "run [class [args]],开始执行应用程序的主类",
            "threads [threadgroup], 列出线程",
            "thread <thread id>, 设置默认线程",
            "suspend [thread id(s)], 挂起线程 (默认值: all)",
            "resume [thread id(s)], 恢复线程 (默认值: all)",
            "where [<thread id> | all], 转储线程的堆栈",
            "wherei [<thread id> | all], 转储线程的堆栈, 以及 pc 信息",
            "up [n frames], 上移线程的堆栈",
            "down [n frames], 下移线程的堆栈",
            "kill <thread id> <expr>, 终止具有给定的异常错误对象的线程",
            "interrupt <thread id>, 中断线程",
            "print <expr>, 输出表达式的值",
            "dump <expr>, 输出所有对象信息",
            "eval <expr>, 对表达式求值 (与 print 相同)",
            "set <lvalue> = <expr>, 向字段/变量/数组元素分配新值",
            "输出当前堆栈帧中的所有本地变量",
            "列出当前已知的类",
            "class <class id>, 显示已命名类的详细资料",
            "methods <class id>, 列出类的方法",
            "fields <class id>, 列出类的字段",
            "列出线程组",
            "threadgroup <name>, 设置当前线程组",
            "clear <class id>.<method>[(argument_type,...)], 清除方法中的断点; "
            + "clear <class id>:<line>, 清除行中的断点; clear, 列出断点",
            "catch [uncaught|caught|all] <class id>|<class pattern>, 出现指定的异常错误时中断",
            "ignore [uncaught|caught|all] <class id>|<class pattern>, 对于指定的异常错误, 取消 'catch'",
            "step, 执行当前行;step up, 一直执行, 直到当前方法返回到其调用方",
            "执行当前指令",
            "list [line number|method], 输出源代码",
            "use (或 sourcepath) [source file path], 显示或更改源路径",
            "exclude [<class pattern>, ... | \"none\"], 对于指定的类, 不报告步骤或方法事件",
            "从目标 VM 输出类路径信息",
            "lock <expr>, 输出对象的锁信息",
            "threadlocks [thread id], 输出线程的锁信息",
            "通过当前帧出栈, 且包含当前帧",
            "与 pop 相同, 但重新进入当前帧",
            "redefine <class id> <class file name>, 重新定义类的代码",
            "disablegc <expr>, 禁止对象的垃圾收集",
            "enablegc <expr>, 允许对象的垃圾收集",
            "重复执行最后一个命令",
            "输出版本信息",
            "stop in <class id>.<method>[(argument_type,...)],在方法中设置断点;stop at <class id>:<line>,在行中设置断点",
            "步进一行 (步过调用)","从断点处继续执行"})
public class JDBCommand extends AbstractXPocketCommand {

    private JDBPlugin plugin;
    
    @Override
    public void invoke(XPocketProcess process) throws Throwable {
        plugin.invoke(process);
    }

    @Override
    public boolean isAvailableNow(String cmd) {
        return plugin.isAvaibleNow(cmd);
    }

    @Override
    public void init(XPocketPlugin plugin) {
        this.plugin = (JDBPlugin)plugin;
    }

    
    
}
