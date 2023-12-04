use dictionary;

create table myWord (
  stt int(11)  auto_increment not null,
  english varchar(15) not null,
  def text not null,
  status int(11),
  primary key(stt)
);

insert into myword (english,definition)
values ('dog','chó');