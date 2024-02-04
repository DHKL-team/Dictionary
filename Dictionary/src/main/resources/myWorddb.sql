use dictionary;

create table myWord (
  stt int(11)  auto_increment not null,
  english varchar(15) not null,
  definition text not null,
  status int(11),
  primary key(stt)
);
