trainer twice_mode_bayes;
    data
        a, b, mode using file(twice_abmode.csv);
    model
        features: b - 2 * a;
        labels: mode;
        algorithm: BayesClassifier;
        training: both;
end