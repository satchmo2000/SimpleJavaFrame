# 极简Java框架的使用方法

用途：本架构主要基于采用存储过程访问数据库的读写封装，控制器采用仿照.Net的调用方式，最大程度地简化用户的开发模式

优势：把业务封装在存储过程里，页面开发时仅需关心页面的逻辑控制，而单一存储将完成权限认证、功能执行许可、功能执行、日志操作及最后的错误码的返回

框架使用实例：https://github.com/satchmo2000/SimpleMavenWeb

1、Services.ClientServicesX是对数据库的增、改、删、查等操作进行封装

   使用方法可以参考ClientServicesM，其中实体可以通过GetProcDefine直接生成

   也可以直接使用ClientServicesBase，这个类没有使用个性化的应用习惯，可以根据自己的习惯进行扩展应用

2、Services.BaseView是对页面控制器Controller进行了封装，通过对RequestMapping进行jsp文件的定位

   使用方法可以参考MainController

3、Utils的核心类

3.1、RSAEncrypt提供RSA加密算法

3.2、EncryptUtil提供安全的防监听的加密思路

   机制：用户登录时，将登录秒数、密码加密串片段等信息进行加密，并进行不定次循环加密，直到末位与第一次的末位相同，再提交给服务器解码核对
   
3.3、QRCodeUtil提供二维码生成集成

3.4、DBConfig提供系统配置，以DBConfig.xml形式存储于resources目录下，格式如下：

<application>
  
    <propertys>
    
        <property name="Server" value="task.bjrtjc.com"/>
        
    </propertys>
    
</application>

4、存储过程实例

 --获取系统负载列表(按小时获取)
 
 --创建日期：2024-03-07
 
 CREATE PROCEDURE ASS_SERVERLOAD_GETRANGE(
 
	@TOKENID	VARCHAR(36) = NULL,--用户登录TOKEN，登录时临时生成，用于获取用户的权限信息
 
	@STARTDATE	DATETIME = NULL ,
 
	@STOPDATE	DATETIME = NULL ,
 
	@RET		INT OUTPUT)--返回执行的错误码
 
AS

BEGIN

	--第一步：根据TOKENID获取用户信息
 
	SET @RET = 0
 
	DECLARE @USERID INT
 
	EXEC HR_USERID_GETREFRESH @TOKENID , 0, @USERID OUTPUT
 
	IF @USERID IS NULL OR @USERID < 1
 
	BEGIN
 
		SET @RET = -2
  
		RETURN
  
	END
 
--第二步：根据用户信息判断当前权限及执行有效性

--按需判断及操作，可以为空

--第三步：数据获取或事务操作（按需增加BEGIN TRANSACTION;等以提高系统执行的完整性）
    
    --获取记录集（支持仅分页部份内容）
    
	SELECT     CONVERT(VARCHAR(13), CREATEDATE, 21) + ':00:00+00:00' AS CREATEDATE, IP, MAX(CPU) AS CPU, MAX(MEMORY) AS MEMORY, MAX([DISK]) AS DISK
 
	FROM         ASS_SERVERLOAD
 
	WHERE     (CREATEDATE BETWEEN @STARTDATE AND @STOPDATE) AND (DELETED = 0)
 
	GROUP BY CONVERT(VARCHAR(13), CREATEDATE, 21), IP
 
	ORDER BY IP, CONVERT(VARCHAR(13), CREATEDATE, 21)

    --获取记录订数量（不分页时，该操作可以没有，系统默认获取记录集的条数）
    
	SELECT     COUNT(*)
 
	FROM         ASS_SERVERLOAD
 
	WHERE     (CREATEDATE BETWEEN @STARTDATE AND @STOPDATE) AND (DELETED = 0)

--4、写入操作日志

	DECLARE @MESSAGE VARCHAR(512)
 
	SET @MESSAGE = dbo.FORMATSTRING2('获取系统负载极大值列表，检索条件={1}~{2}' , @STARTDATE, @STOPDATE)
 
	EXEC SYS_LOG_INSERT @TOKENID , 542 , @MESSAGE , @RET
 
END

GO
