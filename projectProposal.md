# projectProposal


## 1.	Motivation <br>
Miners are a very important part of bitcoin mechanism. The motivation of miners is profit. Their profit comes from two parts, block  reward and transaction fees. Since after mining every 210000 blocks, block rewards will be halved (staring from 50 BTC, now is 12.5    BTC). With the decreasing of block rewards, transaction fees are becoming more and more important. With higher transaction fees, the transaction may be accepted by miners sooner. And the price of bitcoin can also influence the activity of bitcoin market. Therefore, we want to build a model between block rewards, transaction fees, price, and waiting time. Transaction makers and   miners will make their choice according to their profits. <br>

## 2.	Model <br>
### 2.1	Poisson Process <br>
A Poisson Process is a model for a series of discrete event where the average time between events is known, but the exact timing of events is random. The arrival of an event is independent of the event before. <br>
For users who submit transactions in blockchain, they play a game in which they can choose a higher transaction fee to move up in the waiting queue and thus reduce their waiting time, or they cannot pay a fee and accept a longer waiting time. Accordingly, we can view it as a queuing problem with random service and apply Poisson Process to analyze this situation. We suppose that there are N potential users and the opportunity to engage in bitcoin transactions arises independently for each of them at Poisson rate γ. So, transactions flow into the pool at rate γN. <br>
Also, for miners, we assume that they independently working on the problem and the success in solving the problem arrives according to a Poisson distribution with arrival rate λ. Given that the implied parameters from equilibrium, M*, λ∗, transactions flow out at rate λ∗M∗. <br><br>
The dynamics of the pool are straightforward: <br>
If γN > λ∗M*, the size of the pool grows without bound; <br>
If γN < λ∗M*, there is an equilibrium distribution of pool size with mean N ∗= ρ/ (1 −ρ), where ρ= γN/ λ∗M ∗ and mean waiting time w* = [ λ∗M∗(1 −ρ)] −1 where ρ= γN/λ∗M. <br><br>
Furthermore, the fee game has Nash equilibria in which none, some, or all users pay the fee. If all users offer the same fee, then nothing changes other than a transfer from users to miners and an increase in the equilibrium number of miners. If some users offer higher fees than other users offer, then their transactions are selected ahead of those who offer lower, or zero, fees. <br>
(1)	Users who offer a fee have their transactions recorded before any transactions without fees, so the fraction of non-fee-paying users does not affect the waiting time for fee-paying users. <br>
(2)	The non-fee-paying users face a different situation as their expected waiting time depends on both the number of fee-paying and non-fee-paying users. <br>
According to these assumptions above, we can derive a parameter summarizing the costs and benefits to the users of paying the fee and no-fee mean size of the pool and the expected waiting time for a miner to arrive. Then analyze different transaction fee levels in different Nash equilibriums. <br> <br>

### 2.2 Nash Equilibrium <br>
Nash equilibrium means that each player is assumed to know the equilibrium strategies of the other players, and no player has anything to gain by changing only their own strategy. <br>
As for miners in the transaction, they are assumed to be risk-neutral. And they will have the same choice in the game, which means each player's equilibrium strategy is to achieve the maximum of his expected return. Since all other players follow this strategy, miners need to make zero expected profit given the choices of all other potential miners which can be shown in the following equation: <br>
Cost of miners = Transaction fees + Block reward <br>
We can get the equilibrium number of miners and arrival rate of success.<br>
In the similar way, transaction makers in the transaction can also get Nash equilibrium by making zero profit and the equilibrium will change with the change of the rate of paying transaction fees. <br>
Besides, we are also planning to do empirical research about which factors will influence transaction fees.<br><br>

## 3.	Why chose these models <br>
The process of bitcoin mechanism can be regarded as a game process because of all the participants are driven by profit and they are all clear about the which kinds of information that other participants can have. Besides, to assume a real and honest block chain, it is reasonable to assume a non-cooperative game. Therefore, using Nash equilibrium can explain bitcoin mechanism well.<br>
Besides, considering that miners are symmetric and assumed to have identical hashing, the Poisson process can estimate the arriving rate of success for each miner. Similarly, Poisson can properly fit the flowing speed of the transactions and the order in which transactions are removed from the pool does not affect the expected size of the pool, so standard queueing theory results can be applied to determine the pool size.<br>
As for other models, the main model is using economy model to simulate the supply and demand. We think the economy model do not consider specific participants and it is too general and results maybe too fixed. <br>





### References <br>
Huberman, G., Leshno, J.D., Moallemi, C., 2017. Monopoly Without a Monopolist: An Economic Analysis of the Bitcoin Payment System. Unpublished working paper. Columbia Business School, New York. <br>
David Easley, D., O’Hara, M., Basu, S., 2019. The evolution of bitcoin transaction fees. Journal of Financial Economics, 134 (2019) 91–109<br>
Houy, N.,2014. The economics of Bitcoin transaction fees. <br>
Lui, F. 1985. An equilibrium queuing model of bribery. Journal of political economy, 93(4), 760–781.<br>
Nakomoto, S., 2008. A Peer-to-peer Electronic Cash System. <br>
Little, J.D.C., 1961. A proof for the queuing formula: l=λW. Operation Research, 9 (3), 383–387.

