import data
import matplotlib.pyplot as plt
from textwrap import wrap

nbGroups = 5
delta = (data.maxAvgStudents - data.minAvgStudents) / nbGroups
bounds = []

group_students_hard = {}
for problemTuple in sorted(data.benchmarks_hard):
    avgNbStudents = sum(problemTuple[1][1]) / len(problemTuple[1][1])
    key = int((avgNbStudents - data.minAvgStudents) // delta)
    if key == nbGroups: key -= 1
    group_students_hard.setdefault(key, []).append(problemTuple[1])

print('Groups for avg nb students graphs:')
for idx, group_data in (sorted(group_students_hard.items())):
    # Find the lowest and highest values within the group
    min_avg_students = min(sum(item[1]) / len(item[1]) for item in group_data)
    max_avg_students = max(sum(item[1]) / len(item[1]) for item in group_data)
    bounds.append((min_avg_students, max_avg_students))
    # Print to check
    print(f"Group {idx}: Lowest avgNbStudents: {min_avg_students}, Highest avgNbStudents: {max_avg_students}")

group_students_soft = {}
for problemTuple in sorted(data.benchmarks_soft):
    avgNbStudents = sum(problemTuple[1][1]) / len(problemTuple[1][1])
    key = int((avgNbStudents - data.minAvgStudents) // delta)
    if key == nbGroups: key -= 1
    group_students_soft.setdefault(key, []).append(problemTuple[1])

def createNbStudentsGroupsGraphsSoft():
    for groupNb, group_soft in group_students_soft.items():
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
        plt.subplots_adjust(top=0.85)
        plt.suptitle('Evolution of maximisations over time for problems with ' + f"{bounds[groupNb][0]:.1f}" + '-' + f"{bounds[groupNb][1]:.1f}" + ' students per class in average', fontweight="bold", fontsize=11, wrap=True)
        plt.xlabel('Time taken (seconds)')
        fig.text(0.04, 0.5, 'Maximisation rate for each solution', va='center', rotation='vertical')
        plt.savefig('groupedAvgNbStudents' + f"{bounds[groupNb][0]:.1f}" + '-' + f"{bounds[groupNb][1]:.1f}" + '_soft_.png')
        plt.clf()
        plt.cla()

def createNbStudentsGroupsGraphsHard():
    for groupNb, group_hard in group_students_hard.items():
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
        
        plt.title('Evolution of maximisation over time for problems with an average of ' + f"{bounds[groupNb][0]:.1f}" + '-' + f"{bounds[groupNb][1]:.1f}" + ' students per class', wrap=True)
        plt.xlabel('Time taken (seconds)')
        plt.ylabel('Classes met maximisation rate for each solution')
        plt.savefig('groupedAvgNbStudents' + f"{bounds[groupNb][0]:.1f}" + '-' + f"{bounds[groupNb][1]:.1f}" + '_hard_.png')
        plt.clf()

def createGraphs():
    createNbStudentsGroupsGraphsSoft()
    createNbStudentsGroupsGraphsHard()