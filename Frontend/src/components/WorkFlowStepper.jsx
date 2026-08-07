import "./WorkflowStepper.css";

const NORMAL_STEPS = [
  "SUBMITTED",
  "UNDER_REVIEW",
  "APPROVED",
  "IMPLEMENTATION_PENDING",
  "IMPLEMENTED",
  "CLOSED",
];

function formatStatus(status) {
  return status
    .replaceAll("_", " ")
    .toLowerCase()
    .replace(/\b\w/g, (letter) => letter.toUpperCase());
}

function WorkflowStepper({ status }) {

  if (status === "REJECTED") {
    return (
      <div className="workflow-stepper">

        <div className="workflow-step completed">
          <div className="step-circle">✓</div>
          <span>Submitted</span>
        </div>

        <div className="step-line completed-line" />

        <div className="workflow-step completed">
          <div className="step-circle">✓</div>
          <span>Under Review</span>
        </div>

        <div className="step-line rejected-line" />

        <div className="workflow-step rejected">
          <div className="step-circle">✕</div>
          <span>Rejected</span>
        </div>

      </div>
    );
  }

  const currentIndex = NORMAL_STEPS.indexOf(status);

  return (
    <div className="workflow-stepper">

      {NORMAL_STEPS.map((step, index) => {

        const completed = index < currentIndex;
        const current = index === currentIndex;

        return (
          <div className="step-wrapper" key={step}>

            <div
              className={`workflow-step ${
                completed
                  ? "completed"
                  : current
                  ? "current"
                  : ""
              }`}
            >
              <div className="step-circle">
                {completed ? "✓" : index + 1}
              </div>

              <span>{formatStatus(step)}</span>
            </div>

            {index < NORMAL_STEPS.length - 1 && (
              <div
                className={`step-line ${
                  index < currentIndex
                    ? "completed-line"
                    : ""
                }`}
              />
            )}

          </div>
        );
      })}

    </div>
  );
}

export default WorkflowStepper;