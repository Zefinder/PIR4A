import os, glob

global benchmarks
global benchmarks_soft
global benchmarks_hard

global maxStudents
global minStudents

global minAvgStudents
global maxAvgStudents

benchmarks = []
benchmarks_soft = []
benchmarks_hard = []

nbStudents = []
avgNbStudents = []

# parsing the data
for filename in glob.glob('*.txt'):
    with open(os.path.join(os.getcwd(), filename), 'r') as benchmark:
        [nbClasses, classes, paramsString, solutions] = benchmark.readlines()
        params = paramsString.split()
        classesList = [int(c) for c in classes.split()]
        nbStudents.append(sum(classesList))
        avgNbStudents.append(sum(classesList) / len(classesList))
        problem = [int(nbClasses), classesList, int(params[0]), params[1] == 'true', int(params[2]), int(params[3])]
        
        listSolutions = []
        for stats in solutions.split(";"):
            if stats.strip():  # Check if stats is not empty after removing whitespaces
                listSolutions.append([float(x) for x in stats.split()])
        problem.append(listSolutions)

        key = int(nbClasses)*1000 + sum(classesList)
        benchmarks.append((key, problem))

        if problem[3]:
            benchmarks_soft.append((key, problem))
        else:
            benchmarks_hard.append((key, problem))

maxStudents = max(nbStudents)
minStudents = min(nbStudents)

minAvgStudents = min(avgNbStudents)
maxAvgStudents = max(avgNbStudents)