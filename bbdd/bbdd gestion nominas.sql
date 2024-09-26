--creacion y uso base de datos
create DATABASE gestion_nominas;
use gestion_nominas;
--creacion de tablas y resticciones

create table empleados (
    dni varchar(9) not null,
    nombre varchar(50) not null,
    sexo char(1),
    categoria int (2),
    anyos int (2),
    CONSTRAINT pk_e PRIMARY KEY (dni),
    CONSTRAINT sex_chck CHECK (sexo='F' || sexo='M')
);

create table categoria (
    categoria int(2) primary key,
    sueldo decimal (8,2)
);

create table nominas (
    dni varchar(9) primary key,
    categoria int(2) not null,
    sueldo_final decimal(8,2),
    CONSTRAINT fk_d foreign key (dni) references empleados(dni),
    CONSTRAINT fk_c foreign key (categoria) references categoria(categoria)
);

insert INTO empleados values ('32000032G', 'James Cosling', 'M', 9, 7);
insert INTO empleados values ('32000031R', 'Ada Lovelace', 'F', 1, 1);


insert into categoria values (1, 50000);
insert into categoria values (2, 70000);
insert into categoria values (3, 90000);
insert into categoria values (4, 110000);
insert into categoria values (5, 130000);
insert into categoria values (6, 150000);
insert into categoria values (7, 170000);
insert into categoria values (8, 190000);
insert into categoria values (9, 210000);
insert into categoria values (10, 230000);



insert into nominas (dni, categoria, sueldo_final)
select 
e.dni, e.categoria, c.sueldo+(5000 * e.anyos)
from empleados e join categoria c on e.categoria = c.categoria;



