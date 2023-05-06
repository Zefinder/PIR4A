import os, glob

global benchmarks
global benchmarks_soft
global benchmarks_hard

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