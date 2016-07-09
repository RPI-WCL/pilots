from bayes import *

def main():
    bayes = Bayes()
    bayes.load_data("data/bayes_training.txt")
    bayes.training(np.asarray(bayes.data["b"])/np.asarray(bayes.data["a"]), bayes.data["type"])
    bayes.load_data("data/bayes_testing.txt")
    #bayes.testing(np.asarray(bayes.data["d"])/np.asarray(bayes.data["c"]))
    bayes.save_training_para("training.txt")
    bayes.load_training_para("training.txt")
    json_string = bayes.to_json()
    bayes.load_json(json_string)
    bayes.online_learning(np.asarray(bayes.data["d"])/np.asarray(bayes.data["c"]))

if __name__ == "__main__":
    main()
