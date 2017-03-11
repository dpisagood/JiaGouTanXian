package org.smart4j.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类操作的工具类，用来加载类
 * Created by DP on 2016/10/23.
 */
public final class ClassUtil {
    private static final Logger LOGGER= LoggerFactory.getLogger(ClassUtil.class);

    /**
     * 获取类加载器
     * @return 返回当前线程的类加载器
     */
    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类
     * @param className
     * @param isInitialized
     * @return
     */
    public static Class<?> loadClass(String className,boolean isInitialized){
        Class<?> cls;
        try {
            cls=Class.forName(className,isInitialized,getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.error("load class failure",e);
            throw new RuntimeException(e);
        }
        return cls;
    }

    /**
     * 获取指定包名下的所有类的Class对象，即加载指定包名下的所有的类
     * @param packageName 包名
     * @return 返回存储了class对象的Set集合
     */
    public static Set<Class<?>> getClassSet(String packageName){
        Set<Class<?>> classSet=new HashSet<Class<?>>();
        try {
            //URL路径会包括协议部分，得到将是file:/C:/../../../或者是jar:/C:/../../../
            Enumeration<URL> urls=getClassLoader().getResources(packageName.replace(".","/"));//替换cn.iamdp.package成cn/iamdp/package
            while(urls.hasMoreElements()){
                URL url=urls.nextElement();
                if(url!=null){
                    String protocol=url.getProtocol();//得到url的协议部分
                    if(protocol.equals("file")){
                        //因为进行URL编码之后用url.getPath()方法得到的路径中的空格会被替换成%20，
                        // 所以在使用它的时候要进行处理，换成utf-8之后，中文字符问题和这个问题一并解决
                    	//String packagePath=url.getPath().replace("%20", " ");//书上的方法
                        //url.getPath()是得到url的路径的部分
//                        String packagePath= URLDecoder.decode(url.getPath(), "utf-8");
                        String packagePath=CodecUtil.decodeURL(url.getPath());//在框架中写的工具类中的方法，具体实现是上面一句
                        addClass(classSet,packagePath,packageName);
                    }else if (protocol.equals("jar")) {//如果是加载jar包
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();//打开连接
                        if (jarURLConnection != null) {
                            JarFile jarFile = jarURLConnection.getJarFile();
                            if (jarFile != null) {
                                //得到每级的文件路径和文件，等于这一步就得到这个jar包的所有文件和所有文件路径
                                Enumeration<JarEntry> jarEntries = jarFile.entries();
                                while (jarEntries.hasMoreElements()) {
                                    JarEntry jarEntry = jarEntries.nextElement();
                                    String jarEntryName = jarEntry.getName();
                                    if (jarEntryName.endsWith(".class")) {//是class文件
                                        //由路径得到包名,将/换成 .
                                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                        doAddClass(classSet, className);//加载
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return classSet;
    }

    /**
     * 找到指定包名下和其子目录下的所有class文件，并调用doAddClass进行加载
     * @param classSet 存储加载的class对象
     * @param packagePath 包路径
     * @param packageName 包名
     */
    private static void addClass(Set<Class<?>> classSet, String packagePath , String packageName) {
        //根据文件过滤条件加载packageName下的所有.class文件和二级目录
        final File[] files=new File(packageName).listFiles(new FileFilter() {//过滤如果是目录或者是以.class结尾的文件话通过
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
            }
        });
        for(File file:files){
            String fileName=file.getName();//得到文件或者目录的名字
            if(file.isFile()){//如果是文件的话
                 String className=fileName.substring(0,fileName.lastIndexOf("."));
                if(StringUtil.isNotEmpty(packageName)){
                    className=packageName+"."+className;//包名加上文件名才叫完整的文件路径
                }
                doAddClass(classSet,className);//加载这个class文件到classSet中
            }else {//如果是目录的话，重新进行处理
                String subPackagePath=fileName;//得到目录名
                if(StringUtil.isNotEmpty(packagePath)){
                    subPackagePath = packagePath + "/" + subPackagePath;//组成新路径
                }
                String subPackageName=fileName;//得到目录名
                if(StringUtil.isNotEmpty(packageName)){
                    subPackageName=packageName+"."+subPackageName;//组成新包名
                }
                addClass(classSet,subPackagePath,subPackageName);//继续递进查询加载
            }
        }
    }

    /**
     * 为了提高加载类的性能将isInitialized的参数设置成false
     * @param classSet
     * @param className
     */
    private static void doAddClass(Set<Class<?>> classSet, String className) {
        Class<?> cls=loadClass(className,false);
        classSet.add(cls);
    }


}
