import os, glob
import matplotlib.pyplot as plt

benchmarks = []

benchmarks_soft = []
benchmarks_hard = []

# parsing the data
for filename in glob.glob('*.txt'):
    with open(os.path.join(os.getcwd(), filename), 'r') as benchmark:
        [nbClasses, classes, paramsString, solutions] = benchmark.readlines()
        params = paramsString.split()
        classesList = [int(c) for c in classes.split()]
        problem = [int(nbClasses), classesList, int(params[0]), params[1] == 'true', int(params[2]), int(params[3])]
        
        listSolutions = []
        for stats in solutions.split(";"):
            listSolutions.append([float(x) for x in stats.split()])
        problem.append(listSolutions)

        key = int(nbClasses)*1000 + sum(classesList)
        benchmarks.append((key, problem))

        if problem[3]:
            benchmarks_soft.append((key, problem))
        else:
            benchmarks_hard.append((key, problem))

# best solution / time graphs
times_hard = []
for problemTuple in sorted(benchmarks_hard):
    times_hard.append(problemTuple[1][-1][-1][0])

times_soft = []
for problemTuple in sorted(benchmarks_soft):
    times_soft.append(problemTuple[1][-1][-1][0])

if (len(times_soft) != len(times_hard)):
    raise ValueError('Not the same number of soft/hard problems!')

absciss = list(range(0, len(times_hard)))
plt.title("Time taken to find the best solution for each problem")
plt.xlabel("Problem number")
plt.ylabel("Time taken (seconds)")
plt.plot(absciss, times_hard, 'b', label="Meeting the same participant not allowed")
plt.plot(absciss, times_soft, 'g', label="Meeting the same participant allowed")
plt.legend()
plt.savefig("solutions.png")