getLog2(Num, R) :- Num < 2, R = 0, !.
getLog2(Num, R) :- Num1 is Num / 2, getLog2(Num1, R1), R is R1 + 1.

map_build(ListMap, TreeMap) :- length(ListMap, Size),
    getLog2(Size, Height1),
    Height is Height1 + 1,
    map_build(ListMap, [], TreeMap, Height, Size, NewSize).

map_build(T, LeftList, uzel(null, null, null, null), _ , 0, 0):- LeftList = T, !.
map_build(T, LeftList, uzel(null, null, null, null), 0, Size, Size):- LeftList = T, !.
map_build(T, LeftList, uzel(Key, Val, LeftSon, RightSon), Height, Size, NewSize) :-
    Size1 is Size - 1,
    Height1 is Height - 1,
    map_build(T, [(Key, Val) | LeftList1], LeftSon, Height1, Size1, NewSize1),
    map_build(LeftList1, LeftList, RightSon, Height1, NewSize1, NewSize).

map_get(uzel(Key, Val, _, _), Key, Val).
map_get(uzel(Key, Val, LeftSon, RightSon), Key1, Val1) :-
    Key > Key1, map_get(LeftSon, Key1, Val1).
map_get(uzel(Key, Val, LeftSon, RightSon), Key1, Val1) :-
    Key < Key1, map_get(RightSon, Key1, Val1).

map_containsKey(uzel(Key, _ , _ , _), Key).
map_containsKey(uzel(Key1, _, LeftSon, RightSon), Key) :-
    not (Key1 = null),
    map_containsKey(LeftSon, Key).
map_containsKey(uzel(Key1, _, LeftSon, RightSon), Key) :-
    not (Key1 = null),
    map_containsKey(RightSon, Key).

map_containsValue(uzel(_, Val, _, _), Val).
map_containsValue(uzel(_, Val1, LeftSon, RightSon), Val) :-
    not (Val1 = null),
    map_containsValue(LeftSon, Val).
map_containsValue(uzel(_, Val1, LeftSon, RightSon), Val) :-
    not (Val1 = null),
    map_containsValue(RightSon, Val).