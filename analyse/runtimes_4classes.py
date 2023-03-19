import matplotlib.pyplot as plt

# tests made for 4 classes
# constraints considered: playing against different classes, playing against different people, playing 6 games

nb_classes = 4
nb_min_students = 12
nb_max_students = 120

nb_students = list(range(nb_min_students, nb_max_students+1, nb_classes))
print(nb_students)

# this was before we added the constraint "if A plays against B, B plays against A"
# runtimes = [0.12, 0.21, 0.32, 0.47, 0.73, 0.98, 1.23, 1.48, 2.50, 2.88, 3.80, 4.14, 5.32, 5.93, 7.04, 8.08, 8.67, 9.40, 10.85, 12.44, 13.53, 13.87, 15.81, 17.47, 18.21, 18.73, 19.83, 20.34]

# get this from TournoiV1.java
runtimes = [0.106, 0.158, 0.241, 0.373, 0.487, 0.675, 0.869, 1.143, 1.419, 1.862, 2.183, 2.517, 3.147, 3.765, 4.434, 4.880, 5.562, 6.940, 7.639, 8.171, 9.590, 9.258, 9.981, 10.901, 11.442, 11.424, 11.965, 12.568]

check = (len(runtimes) == len(nb_students))

if check:
    plt.plot(nb_students, runtimes)
    plt.xlabel("Number of students in total")
    plt.ylabel("Runtime to find a solution")
    plt.title("Runtimes to find a solution for students divided evenly among " + str(nb_classes) + " classes")
    plt.show()
else:
    print("you don't have the right number of runtimes for the tests considered. check the 'runtimes' list.")