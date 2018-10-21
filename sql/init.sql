#--系统信息
drop table if exists sys_info;
create table sys_info(
  id int(11) primary key auto_increment,
  mac varchar(50) not null comment 'mac地址',
  sys_mark varchar(50) not null comment '水印',
  create_time datetime default current_timestamp comment '创建时间',
  update_time datetime default current_timestamp comment '更新时间',
  create_user int(11) not null comment '创建人',
  create_ip varchar(50) not null comment '创建人ip'
) engine=InnoDB default charset=utf8;

#--用户表
drop table if exists user_info;
create table user_info(
  id int(11) primary key auto_increment,
  grade int(11) not null comment '入学年份',
  clazz varchar(50) comment '班级',
  name varchar(50) comment '姓名',
  student_no varchar(50) comment'学号',
  mobile varchar(50) comment '电话',
  email varchar(50) comment '邮箱',
  password varchar(50) comment '密码',
  role smallint(1) comment '角色',
  note varchar(100) comment '备注',
  create_time datetime default current_timestamp  comment '创建时间',
  update_time datetime default current_timestamp  comment '修改时间'
) engine=InnoDB default charset=utf8;

drop table if exists category;
create table category(
  id int(11) primary key auto_increment,
  name varchar(50) comment '分类名',
  parent int(11) comment '父id',
  create_time datetime default current_timestamp  comment '创建时间',
  update_time datetime default current_timestamp  comment '修改时间'
) engine=InnoDB default charset=utf8;

drop table if exists question;
create table question(
  id int(11) primary key auto_increment,
  title text comment '考题',
  options json comment '选项',
  cat_id int(11) comment '类别',
  init_score numeric(5,2) comment '默认分数',
  type smallint(3) comment '题型,1:单选,2:多选,3:填空,4:判断,5:综合',
  analysis text comment '点评',
  answer text comment '答案',
  level smallint(1) comment '考题难度',
  note varchar(100) comment '备注',
  create_time datetime default current_timestamp  comment '创建时间',
  update_time datetime default current_timestamp  comment '修改时间'
)engine=InnoDB default charset=utf8;

#--考卷
drop table if exists exam_paper;
create table exam_paper(
  id int(11) primary key auto_increment,
  name varchar(100) comment '考卷名',
  type smallint(1) comment '考卷类型：1:练习,2:考试',
  start_time datetime not null default current_timestamp comment '考试开始时间',
  end_time datetime not null default current_timestamp comment '考试结束时间',
  total_score numeric(5,2) comment '总分',
  passing numeric(5,2) comment '及格线',
  status smallint(1) comment '考试状态',
  create_time datetime default current_timestamp  comment '创建时间',
  update_time datetime default current_timestamp  comment '修改时间'
)engine=InnoDB default charset=utf8;

#--考卷考题
drop table if exists exam_info;
create table exam_info(
  id int(11) primary key auto_increment,
  exam_id int(11) comment '考卷id',
  question_id int(11) comment '考题id',
  create_time datetime default current_timestamp  comment '创建时间',
  update_time datetime default current_timestamp  comment '修改时间'
)engine=InnoDB default charset=utf8;

#--考场
drop table if exists exam_room;
create table exam_room(
  id int(11) primary key auto_increment,
  paper_id int(11) comment '考卷id',
  user_id varchar(50) comment '用户编号',
  take_time int(11) comment '历时',
  status smallint(1) comment '考试状态',
  create_time datetime default current_timestamp  comment '创建时间',
  update_time datetime default current_timestamp  comment '修改时间'
)engine=InnoDB default charset=utf8;

#--答题卡
drop table if exists exam_log;
create table exam_log(
  id int(11) primary key auto_increment,
  paper_id int(11) comment '考卷id',
  user_id varchar(50) comment '用户编号',
  question_id int(11) comment '问题id',
  answer text comment '用户答案',
  score numeric(5,2) comment '得分',
  teacher_id varchar(50) comment '评分人编号',
  question_no int(5) comment '考题在试卷中的编号(顺序号)',
  create_time datetime default current_timestamp  comment '创建时间',
  update_time datetime default current_timestamp  comment '修改时间'
)engine=InnoDB default charset=utf8;

#--成绩
drop table if exists score;
create table score(
  id int(11) primary key auto_increment,
  room_id int(11) comment '考场id',
  user_id varchar(50) comment '用户编号',
  score numeric(5,2) comment '汇总成绩',
  status smallint(1) comment '成绩状态，0:未汇总，1:已汇总,2:已排名',
  award_status smallint(1) comment '颁证状态，0:不能发证，1:授予证书',
  print_status smallint(1) comment '打印状态,0:未打印，1:已打印',
  reate_time datetime default current_timestamp  comment '创建时间',
  update_time datetime default current_timestamp  comment '修改时间'
)engine=InnoDB default charset=utf8;
#表更改记录
alter table category add column sequence varchar(200) comment '程序拼接的序列';
alter table user_info add column status smallint(3) default 0 comment '用户状态,-1:已删除,0:已注册,1:审批通过';
alter table exam_log add column status smallint(3) default 0 comment '考题批改状态,0:未批改,1:已批改';
alter table exam_paper add column max_time int(11)  comment '考试时长,单位:秒';
#管理员
insert into user_info(grade,name,student_no,mobile,email,password,role,status)values(0,'hans','10000','13269095668','liuyuanzhi126@126.com','123456',2,1);
#毒品类别
insert into category(name,parent,sequence)values('毒品知识',0,'1');
insert into category(name,parent,sequence)values('戒毒知识',0,'2');
insert into category(name,parent,sequence)values('禁毒知识',0,'3');
insert into category(name,parent,sequence)values('公益',0,'4');


#判断
insert into question(title, cat_id, init_score, type, analysis, answer, level) VALUES ('杜冷丁，又称哌替啶，为白色结晶性粉末，无臭或几乎无臭。医疗多用于针剂，滥用会成瘾，严重危害人体健康和生命安全。',1,3,4,'','1',2);
#判断
insert into question(title, cat_id, init_score, type, analysis, answer, level) VALUES ('鸦片是一种无色、无味的物质。',1,3,4,'','1',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('“国际禁毒日”是每年的（  ）。','{"A":"7月9日","B":"6月26日","C":"12月1日","D":"5月17日"}',2,3,1,'','B',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('“金三角”是指泰国、缅甸、（ ）。','{"A":"老挝","B":"越南","C":"柬埔寨","D":"印度"}',3,3,1,'','A',1);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('“摇头丸”是苯丙胺类的衍生物，是亚甲基二氧甲基苯丙胺的片剂，属中枢神经（  ），是我国规定管制的精神药品。“摇头丸”是其俗称，意为会使人摇头的药丸，吸食者易处于幻觉状态，有暴力攻击倾向。','{"A":"麻醉剂","B":"兴奋剂","C":"抑制剂","D":"以上答案都不正确"}',1,3,1,'','B',2);
#判断
insert into question(title, cat_id, init_score, type, analysis, answer, level) VALUES ('按药物对人体大脑中枢神经所产生的作用，可将毒品分为麻醉药品和精神药品。',1,3,4,'','0',2);
#判断
insert into question(title, cat_id, init_score, type, analysis, answer, level) VALUES ('苯丙胺因其纯品无色透明，像冰一样，故俗称“冰毒”。',1,3,4,'','0',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('不属于阿片类毒品的是（  ）','{"A":"吗啡","B":"海洛因","C":"冰毒","D":"美沙酮"}',1,3,1,'','C',2);
#判断
insert into question(title, cat_id, init_score, type, analysis, answer, level) VALUES ('氟硝安定、安眠酮、丁丙诺啡、地西泮及有机溶剂和鼻吸剂是合成毒品。',1,3,4,'','1',2);
#判断
insert into question(title, cat_id, init_score, type, analysis, answer, level) VALUES ('经强制隔离戒毒后又吸食，注射毒品的，构成吸食毒品罪。',2,3,4,'','0',2);
#判断
insert into question(title, cat_id, init_score, type, analysis, answer, level) VALUES ('强制隔离戒毒场所的设置、管理体制和经费保障，可由各地政府视财政情况自行规定。',2,3,4,'','0',2);
#判断
insert into question(title, cat_id, init_score, type, analysis, answer, level) VALUES ('吸毒成瘾人员自愿接受强制隔离戒毒的，经公安机关同意，可以进入强制隔离戒毒场所戒毒。',2,3,4,'','1',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('（  ）俗称K粉，属于合成类新型毒品。','{"A":"氯胺酮","B":"海洛因","C":"大麻","D":"美沙酮"}',1,3,1,'','A',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('（  ）化学名称叫二乙酰吗啡,呈灰白色粉末状,也就是人们所说的“白粉”、“白面”。','{"A":"冰毒","B":"可卡因","C":"海洛因","D":"美沙酮"}',1,3,1,'','C',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('（  ）,即甲基苯丙胺,外观为纯白或黄色结晶体,晶莹剔透。','{"A":"可卡因","B":"冰毒","C":"海洛因","D":".吗啡"}',1,3,1,'','B',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('（  ）俗名“可可精”，是从古柯叶中分离出来的一种最主要的生物碱，属中枢神经兴奋剂，呈白色晶体状，无气味，味略苦而麻，兴奋作用强。','{"A":"海洛因","B":"可卡因","C":"大麻","D":"鸦片"}',1,3,1,'','B',2);
#判断
insert into question(title, cat_id, init_score, type, analysis, answer, level) VALUES ('“快克”是一种高纯度的海洛因，属于海洛因中的精制品。',1,3,4,'','0',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('《禁毒法》规定，国家采取各种措施帮助吸毒人员戒除毒瘾，（  ）吸毒人员。吸毒成瘾人员应当进行戒毒治疗。','{"A":"打击处理","B":"依法惩治","C":"教育和挽救","D":"收容看管"}',2,3,1,'','C',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('《禁毒法》规定，国家对麻醉药品和精神药品实行管制，对麻醉药品和精神药品的实验研究、生产、经营、使用、储存、运输实行（  ）制度。','{"A":"审批","B":"审核和检查","C":"许可和查验","D":"以上答案都不正确"}',2,3,1,'','C',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('《禁毒法》规定，国家鼓励对禁毒工作的社会捐赠，并依法给予（  ）。','{"A":"税收减免","B":"税收优惠","C":"表彰奖励","D":"税收减免和优惠"}',2,3,1,'','B',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('《禁毒法》规定，教育行政部门、（  ）应当将禁毒知识纳入教育、教学内容，对学生进行禁毒宣传教育。公安机关、司法行政部门和卫生行政部门应当予以协助。','{"A":"学校","B":"中小学校","C":"大专院校","D":"B和C"}',2,3,1,'','A',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('《禁毒法》规定，禁止非法（  ）麻醉药品、精神药品和易制毒化学品的制造方法。公安机关接到举报或者发现此类情况，应当及时依法查处。','{"A":"贩卖","B":"传播","C":"传授","D":"获取"}',2,3,1,'','C',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('《禁毒法》规定，强制隔离戒毒场所应当根据戒毒人员（　）   等，对戒毒人员进行有针对性的生理、心理治疗和身体康复训练。','{"A":"吸食、注射毒品的方法","B":"吸食、注射毒品的频次","C":"吸食、注射毒品的种类和成瘾程度","D":"吸食、注射毒品的时间"}',2,3,1,'','C',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('《禁毒法》规定，娱乐场所应当建立（  ）制度，发现娱乐场所内有毒品违法犯罪活动的，应当立即向公安机关报告。','{"A":"巡查","B":"巡逻","C":".检查","D":"清查"}',2,3,1,'','A',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('《禁毒法》规定的禁毒工作机制是（　）。','{"A":"党委统一领导，政府各部门各司其责，全社会广泛参与","B":"政府统一领导，有关部门各负其责，社会广泛参与","C":"党委政府统一领导，各有关部门各司其责，社会广泛参与","D":"党委政府领导，各有关部门各负其责，全社会广泛参与"}',2,3,1,'','B',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('《刑法》规定非法种植罂粟（　）株以上构成犯罪。','{"A":"1000","B":"500","C":"5000","D":"8000"}',2,3,1,'','B',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('《中华人民共和国禁毒法》自（）起施行。','{"A":"2007年12月29日","B":"2008年1月1日","C":"2008年6月1日","D":"2008年6月26日"}',2,3,1,'','C',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('《中华人民共和国刑法》规定，毒品的数量以（  ）属实的走私、贩卖、运输、制造、非法持有毒品的数量计算，不以纯度折算。','{"A":"缴获","B":"检查","C":"查证","D":"收缴"}',2,3,1,'','C',2);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('《中华人民共和国刑法》规定，走私、贩卖、运输、制造鸦片不满二百克、海洛因或者甲基苯丙胺不满（  ）克或者其他少量毒品的，处三年以下有期徒刑、拘役或者管制，并处罚金；情节严重的，处三年以上七年以下有期徒刑，并处罚金。','{"A":"十","B":"二十","C":"三十","D":"四十"}',2,3,1,'','A',3);
#单选
insert into question(title, options, cat_id, init_score, type, analysis, answer, level) VALUES('《中华人民共和国刑法》规定，走私、贩卖、运输、制造鸦片一千克以上、海洛因或者甲基苯丙胺（　）克以上或者其他毒品数量大的，处十五年有期徒刑、无期徒刑或者死刑，并处没收财产。','{"A":"十","B":"二十","C":"五十","D":"一百"}',2,3,1,'','C',3);

