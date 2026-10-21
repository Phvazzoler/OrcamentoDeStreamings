create
database if not exists orcamento_streamings;
use
orcamento_streamings;

create table categorias
(
    id   int auto_increment primary key,
    nome varchar(50) not null unique
);

create table assinaturas
(
    id             int auto_increment primary key,
    nome           varchar(100)   not null,
    valor          decimal(10, 2) not null check (valor >= 0),
    ciclo_cobranca enum('Mensal','Anual') not null default 'Mensal',
    dia_cobranca   tinyint        not null check (dia_cobranca between 1 and 31),
    ativa          boolean        not null default true,
    categoria_id   int            not null,
    foreign key (categoria_id) references categorias (id)
);

create table wishlist_itens
(
    id           int auto_increment primary key,
    nome         varchar(100)   not null,
    preco        decimal(10, 2) not null check (preco >= 0),
    prioridade   enum('BAIXA', 'MEDIA', 'ALTA') not null default 'MEDIA',
    comprado     boolean        not null default false,
    link         varchar(255),
    categoria_id int            not null,
    foreign key (categoria_id) references categorias (id)
);

create table limite_orcamento
(
    id           int auto_increment primary key,
    valor_limite decimal(10, 2) not null check (valor_limite > 0)
);

insert into categorias (nome)
values ('Streaming'),
       ('Jogos'),
       ('Colecionáveis');

insert into assinaturas (nome, valor, ciclo_cobranca, dia_cobranca, categoria_id)
values ('Netflix', 44.90, 'Mensal', 10, 1),
       ('Game Pass', 49.99, 'Mensal', 15, 2),
       ('Spotify', 23.90, 'Mensal', 5, 1),
       ('Crunchyroll', 239.88, 'Anual', 20, 1);

insert into wishlist_itens (nome, preco, prioridade, categoria_id)
values ('Elden Ring', 199.90, 'ALTA', 2),
       ('Funko Pop Goku', 89.90, 'MEDIA', 3);

insert into limite_orcamento (valor_limite)
values (200.00);