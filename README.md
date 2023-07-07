Java2 Final Project code in 2023 Spring (two classmates: Luo and Dong)

A SpringBoot web application for displaying analysis and visualization of data crawled from StackOverFlow posts.

如何第一次运行：

1. 在IDEA中右键jieba文件夹，“添加为库”
2. 安装PostgreSQL数据库（推荐14版），修改配置文件 (src/main/resources/application.properties) 中的数据库用户名和密码，并新建一个名为t的数据库
3. 修改上述配置文件，把第6行的none改为create（即注释第6行，取消注释第5行）
4. 修改src/main/java/cse/java2/project/Application.java，取消第22行的注释：  questionService.addAll();
5. 运行上述Application.java
6. 将3、4步的修改复原
7. 运行上述Application.java
