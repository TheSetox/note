function escapeForRegex(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

function classifyArea(path) {
  if (path.startsWith("androidApp/")) return "androidApp";
  if (path.startsWith("desktopApp/")) return "desktopApp";
  if (path.startsWith("iosApp/")) return "iosApp";
  if (path.startsWith("core/common/")) return "core:common";
  if (path.startsWith("core/database/")) return "core:database";
  if (path.startsWith("core/ui/")) return "core:ui";
  if (path.startsWith("feature/notes/")) return "feature:notes";
  if (path.startsWith(".github/")) return ".github";
  if (
    path === "build.gradle.kts" ||
    path === "settings.gradle.kts" ||
    path === "gradle.properties" ||
    path === "gradlew" ||
    path === "gradlew.bat" ||
    path.startsWith("gradle/")
  ) {
    return "build-logic";
  }
  return "other";
}

function buildAreaRows(files) {
  const counts = new Map();
  for (const file of files) {
    const area = classifyArea(file.filename);
    counts.set(area, (counts.get(area) || 0) + 1);
  }

  return Array.from(counts.entries())
    .sort((a, b) => {
      if (b[1] !== a[1]) return b[1] - a[1];
      return a[0].localeCompare(b[0]);
    })
    .map(([area, count]) => `| \`${area}\` | ${count} |`)
    .join("\n");
}

function buildCommitRows(commits, limit = 12) {
  const rows = commits.slice(-limit).reverse().map((commit) => {
    const sha = commit.sha.slice(0, 7);
    const title = (commit.commit.message || "").split("\n")[0].trim() || "no title";
    return `- \`${sha}\` ${title}`;
  });
  return rows.length > 0 ? rows.join("\n") : "- none";
}

function buildFileRows(files, limit = 25) {
  const rows = files.slice(0, limit).map((file) => {
    const status = (file.status || "modified").toUpperCase();
    return `- \`${status}\` \`${file.filename}\` (+${file.additions} / -${file.deletions})`;
  });
  return rows.length > 0 ? rows.join("\n") : "- none";
}

function buildSummarySection({ pullRequest, files, commits }) {
  const sectionStart = "<!-- codex-pr-summary:start -->";
  const sectionEnd = "<!-- codex-pr-summary:end -->";
  const headSha = pullRequest.head.sha.slice(0, 7);
  const generatedAt = new Date().toISOString();

  const lines = [
    sectionStart,
    "## PR Summary (Auto-Updated)",
    "",
    `- Updated at: ${generatedAt}`,
    `- Head SHA: \`${headSha}\``,
    `- Changed files: ${files.length}`,
    `- Commits in PR: ${commits.length}`,
    "",
    "### Module/Area Impact",
    "| Module/Area | Files |",
    "|---|---:|",
    buildAreaRows(files),
    "",
    "### Recent Commits",
    buildCommitRows(commits),
    "",
    "### Changed Files (Top 25)",
    buildFileRows(files),
    "",
    "_This section is maintained by `.github/workflows/pr-summary.yml`._",
    sectionEnd,
  ];

  return lines.join("\n");
}

async function upsertPrSummary({ github, context }) {
  const pullRequest = context.payload.pull_request;
  if (!pullRequest) {
    return;
  }

  const { owner, repo } = context.repo;
  const pull_number = pullRequest.number;

  const [files, commits] = await Promise.all([
    github.paginate(github.rest.pulls.listFiles, {
      owner,
      repo,
      pull_number,
      per_page: 100,
    }),
    github.paginate(github.rest.pulls.listCommits, {
      owner,
      repo,
      pull_number,
      per_page: 100,
    }),
  ]);

  const summarySection = buildSummarySection({ pullRequest, files, commits });
  const sectionStart = "<!-- codex-pr-summary:start -->";
  const sectionEnd = "<!-- codex-pr-summary:end -->";
  const sectionRegex = new RegExp(
    `${escapeForRegex(sectionStart)}[\\s\\S]*?${escapeForRegex(sectionEnd)}`,
    "m"
  );

  const currentBody = pullRequest.body || "";
  const newBody = sectionRegex.test(currentBody)
    ? currentBody.replace(sectionRegex, summarySection)
    : `${currentBody.trim()}\n\n${summarySection}`.trim();

  if (newBody !== currentBody) {
    await github.rest.pulls.update({
      owner,
      repo,
      pull_number,
      body: newBody,
    });
  }
}

module.exports = {
  upsertPrSummary,
};
