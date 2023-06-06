create table public.cart
(
    chat_id    bigint,
    user_order text,
    price      integer
);

create table public.orders
(
    chat_id    bigint,
    user_order text,
    price      integer
);


