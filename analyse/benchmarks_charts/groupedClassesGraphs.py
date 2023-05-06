import data
import matplotlib.pyplot as plt

group_classes_hard = {}
for problemTuple in sorted(data.benchmarks_hard):
    group_classes_hard.setdefault(problemTuple[1][0], []).append(problemTuple[1])

group_classes_soft = {}
for problem in sorted(data.benchmarks_soft):
    group_classes_soft.setdefault(problem[1][0], []).append(problem[1])

def createClassGroupsGraphsSoft():
    for classNb, group_soft in group_classes_soft.items():
        fig, (ax1, ax2) = plt.subplots(2, 1, sharex = True)
        for problem in group_soft:
            times_max = []
            max_students = []
            max_classes = []
            for solution in problem[-1]:
                times_max.append(solution[0])
                max_students.append(solution[1] / problem[4])
                max_classes.append(solution[2] / problem[5])
            ax1.plot(times_max, max_students)
            ax2.plot(times_max, max_classes)
        
        ax1.set_title('Maximising students met')
        ax2.set_title('Maximising classes met')

        fig.subplots_adjust(hspace=0.3)
        plt.suptitle('Evolution of maximisations met over time for problems with ' + str(classNb) + ' classes')
        plt.xlabel('Time taken (seconds)')
        fig.text(0.04, 0.5, 'Maximisation rate for each solution', va='center', rotation='vertical')
        plt.savefig('groupedClass' + str(classNb) + '_soft_.png')
        plt.clf()

def createClassGroupsGraphsHard():
    for classNb, group_hard in group_classes_hard.items():
        for problem in group_hard:
            times_max = []
            max_classes = []
            for solution in problem[-1]:
                times_max.append(solution[0])
                max_classes.append(solution[2] / problem[5])
            if (len(max_classes) != 1):
                plt.plot(times_max, max_classes)
            else:
                plt.plot(times_max[0], max_classes[0], "x") 
        
        plt.title('Evolution of maximisation over time for problems with ' + str(classNb) + ' classes')
        plt.xlabel('Time taken (seconds)')
        plt.ylabel('Classes met maximisation rate for each solution')
        plt.savefig('groupedClass' + str(classNb) + '_hard_.png')
        plt.clf()

def createGraphs():
    createClassGroupsGraphsSoft()
    createClassGroupsGraphsHard()