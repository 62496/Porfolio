import React from 'react';
import { styles } from "../../styles/styles"

const ProgressSteps = ({ currentStep, steps }) => {
    return (
        <div style={styles.progressContainer}>
            {steps.map((step, index) => (
                <div key={index} style={styles.stepItem}>
                    <div
                        style={{
                            ...styles.stepCircle,
                            ...(index < currentStep && styles.stepCircleComplete),
                            ...(index === currentStep && styles.stepCircleActive)
                        }}
                    >
                        {index < currentStep ? '✓' : index + 1}
                    </div>
                    <div style={styles.stepLabel}>{step}</div>
                    {index < steps.length - 1 && (
                        <div
                            style={{
                                ...styles.stepLine,
                                ...(index < currentStep && styles.stepLineComplete)
                            }}
                        />
                    )}
                </div>
            ))}
        </div>
    );
};

export default ProgressSteps;