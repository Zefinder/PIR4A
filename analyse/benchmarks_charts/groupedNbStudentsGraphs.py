import data
import matplotlib.pyplot as plt

nbGroups = 5
delta = (data.maxStudents - data.minStudents) / nbGroups
bounds = []

group_students_hard = {}
for problemTuple in sorted(data.benchmarks_hard):
    nbStudents = sum(problemTuple[1][1])
    key = int((nbStudents - data.minStudents) // delta)
    if key == nbGroups: key -= 1
    group_students_hard.setdefault(key, []).append(problemTuple[1])

print('Groups for nb students graphs:')
for idx, group_data in (sorted(group_students_hard.items())):
    # Find the lowest and highest values within the group
    min_students = min(sum(item[1]) for item in group_data)
    max_students = max(sum(item[1]) for item in group_data)
    bounds.append((min_students, max_students))
    # Print to check
    print(f"Group {idx}: Lowest nbStudents: {min_students}, Highest nbStudents: {max_students}")

group_students_soft = {}
for problemTuple in sorted(data.benchmarks_soft):
    nbStudents = sum(problemTuple[1][1])
    key = int((nbStudents - data.minStudents) // delta)
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
        plt.suptitle('Evolution of maximisations over time for problems with ' + str(bounds[groupNb][0]) + '-' + str(bounds[groupNb][1]) + ' students', fontsize=11, fontweight="bold")
        plt.xlabel('Time taken (seconds)')
        fig.text(0.04, 0.5, 'Maximisation rate for each solution', va='center', rotation='vertical')
        plt.savefig('groupedNbStudents' + str(bounds[groupNb][0]) + '-' + str(bounds[groupNb][1]) + '_soft_.png')
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
        
        plt.title('Evolution of maximisation over time for problems with ' + str(int(bounds[groupNb][0])) + '-' + str(int(bounds[groupNb][1])) + ' students')
        plt.xlabel('Time taken (seconds)')
        plt.ylabel('Classes met maximisation rate for each solution')
        plt.savefig('groupedNbStudents' + str(int(bounds[groupNb][0])) + '-' + str(int(bounds[groupNb][1])) + '_hard_.png')
        plt.clf()

def createGraphs():
    createNbStudentsGroupsGraphsSoft()
    createNbStudentsGroupsGraphsHard()