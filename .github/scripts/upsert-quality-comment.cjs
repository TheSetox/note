function toBadge(value) {
  switch (value) {
    case "success":
      return "✅ success";
    case "failure":
      return "❌ failure";
    case "cancelled":
      return "⚪ cancelled";
    case "skipped":
      return "⚪ skipped";
    default:
      return "⚪ unknown";
  }
}

function hasValue(value) {
  return Boolean(value && value.trim() !== "");
}

function escapeForRegex(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

function buildCheckRows(env) {
  const checks = [
    {
      cmd: env.LINT_CMD,
      label: env.LINT_LABEL,
      result: env.LINT_RESULT,
    },
    {
      cmd: env.DETEKT_CMD,
      label: env.DETEKT_LABEL,
      result: env.DETEKT_RESULT,
    },
    {
      cmd: env.TEST_CMD,
      label: env.TEST_LABEL,
      result: env.TEST_RESULT,
    },
    {
      cmd: env.ASSEMBLE_CMD,
      label: env.ASSEMBLE_LABEL,
      result: env.ASSEMBLE_RESULT,
    },
    {
      cmd: env.KOVER_CMD,
      label: env.KOVER_LABEL,
      result: env.KOVER_RESULT,
    },
  ].filter((check) => hasValue(check.cmd));

  return checks
    .map((check) => `| \`${check.label || check.cmd}\` | ${toBadge(check.result)} |`)
    .join("\n");
}

function buildSection(env) {
  const sectionStart = `<!-- quality-gate-section:${env.MODULE_KEY}:start -->`;
  const sectionEnd = `<!-- quality-gate-section:${env.MODULE_KEY}:end -->`;

  const lines = [
    sectionStart,
    `### ${env.MODULE_TITLE}`,
    "",
    "| Check | Result |",
    "|---|---|",
    buildCheckRows(env),
  ];

  if (hasValue(env.KOVER_CMD) && hasValue(env.KOVER_TABLE)) {
    const coverageTitle = hasValue(env.COVERAGE_TITLE)
      ? env.COVERAGE_TITLE
      : `${env.MODULE_TITLE} Line Coverage`;

    lines.push("");
    lines.push(`### ${coverageTitle}`);
    lines.push(env.KOVER_TABLE.trim());
  }

  lines.push(sectionEnd);
  return lines.join("\n");
}

async function upsertQualityComment({ github, context, env }) {
  const marker = "<!-- quality-gate-report -->";
  const currentShaMarker = `<!-- quality-gate-sha:${context.payload.pull_request.head.sha} -->`;
  const legacyMarkers = [
    "<!-- quality-gate-android -->",
    "<!-- quality-gate-desktop -->",
    "<!-- quality-gate-ios -->",
    "<!-- quality-gate-core-common -->",
    "<!-- quality-gate-core-database -->",
    "<!-- quality-gate-core-ui -->",
    "<!-- quality-gate-feature-notes -->",
  ];

  const sectionStart = `<!-- quality-gate-section:${env.MODULE_KEY}:start -->`;
  const sectionEnd = `<!-- quality-gate-section:${env.MODULE_KEY}:end -->`;
  const sectionRegex = new RegExp(
    `${escapeForRegex(sectionStart)}[\\s\\S]*?${escapeForRegex(sectionEnd)}`
  );

  const { owner, repo } = context.repo;
  const issue_number = context.payload.pull_request.number;

  const comments = await github.paginate(github.rest.issues.listComments, {
    owner,
    repo,
    issue_number,
    per_page: 100,
  });

  let existing = comments.find(
    (comment) => comment.user?.type === "Bot" && comment.body?.includes(marker)
  );

  if (!existing) {
    existing = comments.find(
      (comment) =>
        comment.user?.type === "Bot" &&
        legacyMarkers.some((legacyMarker) => comment.body?.includes(legacyMarker))
    );
  }

  let body;
  if (!existing || !existing.body?.includes(currentShaMarker)) {
    body = `${marker}\n${currentShaMarker}\n### Quality Gate Report`;
  } else {
    body = existing.body.replace(/<!-- quality-gate-sha:[0-9a-f]+ -->/, currentShaMarker);
  }

  const section = buildSection(env).trim();
  if (sectionRegex.test(body)) {
    body = body.replace(sectionRegex, section);
  } else {
    body = `${body.trim()}\n\n${section}`;
  }

  let targetCommentId;
  if (existing) {
    targetCommentId = existing.id;
    await github.rest.issues.updateComment({
      owner,
      repo,
      comment_id: existing.id,
      body,
    });
  } else {
    const created = await github.rest.issues.createComment({
      owner,
      repo,
      issue_number,
      body,
    });
    targetCommentId = created.data.id;
  }

  const cleanupMarkers = [...legacyMarkers, marker];
  for (const comment of comments) {
    if (comment.id === targetCommentId) {
      continue;
    }
    if (comment.user?.type !== "Bot") {
      continue;
    }

    const hasKnownMarker = cleanupMarkers.some((knownMarker) =>
      comment.body?.includes(knownMarker)
    );

    if (hasKnownMarker) {
      try {
        await github.rest.issues.deleteComment({
          owner,
          repo,
          comment_id: comment.id,
        });
      } catch (error) {
        console.warn(
          `Unable to delete stale quality comment ${comment.id}: ${error.message}`
        );
      }
    }
  }
}

module.exports = {
  upsertQualityComment,
};
