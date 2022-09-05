simp_mem(N, H) :-
	H < N,
	M is mod(N, H),
	0 \= M,
	H1 is H + 1,
	simp_mem(N, H1).
simp_mem(N, H) :- H >= N.

init(MAX_N) :- MAX_N > 1,
	H is MAX_N - 1, init(H),
	simp_mem(MAX_N, 2),
	assert(prime(MAX_N)), !.
init(_).

composite(N) :- not prime(N).

prime_divisors(1, []).
prime_divisors(N, [H | T]) :- number(N),
	prime(H),
	0 is mod(N, H),
	N1 is div(N, H), prime_divisors(N1, T), !.

square_divisors(N, D) :- N1 is N * N, prime_divisors(N1, D).

 