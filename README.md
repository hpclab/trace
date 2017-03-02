TRACE
=======

Avatars’ mobility is an essential element to design, validate and compare different distributed virtual environment architectures. It has a direct impact on the management of such systems because it defines the workload associated with the areas in the virtual world. Currently, a relevant part of this evaluation is conducted by means of synthetic traces generated through mobility models. Despite that, in the last decade, several models have been proposed in literature to describe avatars mobility. However, a standard methodology that drives researchers in their evaluation does not yet exist. In order to alleviate this issue, we present TRACE, an open source tool supporting the generation and analysis of traces by means of embedded mobility models. TRACE’s ultimate aim is to facilitate the evaluation and comparison of virtual environments and allow researchers to focus on developing their solution rather than spend time to code and test custom mobility traces. TRACE provides a unified format to describe the traces. It enables scalable and efficient trace generation and analysis for thousands of avatars with seven built-in models. Also, it defines APIs enabling the integration of additional models, different configurations of the environment and several built-in metrics to analyze the generated traces.

### Publications

**2016 - IEEE Transaction on Parallel and Distributed Systems**

Carlini, Emanuele and Lulli, Alessandro and Ricci, Laura
**TRACE: generating traces from mobility models for Distributed Virtual Environments** 
IEEE Transactions on parallel and distributed systems (2016) (to appear).

@article{carlini2016trace,
	title={TRACE: generating traces from mobility models for Distributed Virtual Environments},
	author={Carlini, Emanuele and Lulli, Alessandro and Ricci, Laura},
	booktitle={Euro-Par 2016: Parallel Processing Workshops},
	pages={},
	year={2016},
	organization={Springer},
	addendum={\emph{to appear}}
}

### How to build

mvn clean package

### How to configure
It is required two files:
1) a mapping file called mobilityModel.conf between model name and the class that implement the model

model.lapt ema.dve.workload.mobility.latp.Lapt
model.randomwalk ema.dve.workload.mobility.random.RandomWalk
model.randomwaypoint ema.dve.workload.mobility.random.RandomWayPoint
model.bluebanana ema.dve.workload.mobility.secondlife.BlueBananaParallel
model.rpgm ema.dve.workload.mobility.rpgm.Rpgm
model.mixed ema.dve.workload.mobility.mixed.Mixed
model.sympathy ema.dve.workload.mobility.sympathy.Sympathy

2) a configuration file for the model. Following an example:

model bluebanana
AVATAR 200
HOTSPOT_NUM 20
HOTSPOT_RADIUS 20
ITERATION_NUM 200
MAP_WIDTH 1500
MAP_HEIGHT 1500
AOI_RADIUS 20
ENABLE_DUMP true
ENABLE_GRAPHIC true


### How to run

java -cp target/traceGenerator-jar-with-dependencies.jar test.Main config-file-specification-2