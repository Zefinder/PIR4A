import matplotlib.pyplot as plt

# tests made for 4 classes
# constraints considered: playing against different classes, playing against different people, playing 6 games

nb_classes = 6
nb_min_students = 6
nb_max_students = 150

nb_students = list(range(nb_min_students, nb_max_students+1, nb_classes))
print(nb_students)

# this was before we added the constraint "if A plays against B, B plays against A"
# runtimes = [0.054, 0.134, 0.297, 0.573, 0.924, 1.441, 2.647, 3.230, 4.770, 5.776, 7.428, 8.774, 11.135, 12.841, 16.141, 16.192, 18.718, 20.892, 24.917, 24.094, 24.703, 26.189, 29.508, 31.142, 34.443]

# get this from TournoiV1.java
runtimes = [-1.000, 0.126, 0.227, 0.395, 0.623, 1.019, 1.571, 1.948, 2.647, 3.473, 4.418, 5.537, 6.974, 8.714, 9.545, 9.433, 11.293, 12.079, 12.558, 14.486, 15.721, 16.582, 17.703, 18.548, 20.050]

check = (len(runtimes) == len(nb_students))

if check:
    plt.plot(nb_students, runtimes)
    plt.xlabel("Number of students in total")
    plt.ylabel("Runtime to find a solution (s)")
    plt.title("Runtimes to find a solution for students divided evenly among " + str(nb_classes) + " classes")
    plt.show()
else:
    print("you don't have the right number of runtimes for the tests considered. check the 'runtimes' list.")