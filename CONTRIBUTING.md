# Make your contribution!

Any help is welcome and highly appreciated! If you want to contribute to the repository, the following instructions will help you get started...

## Development
### Necessary tools
- [Intelij Idea](https://www.jetbrains.com/idea/)
or 
- any other editor that supports Java app development
- [Git](https://git-scm.com/downloads)
- [Maven](https://maven.apache.org)

## Getting to work

1. [Fork](https://help.github.com/articles/fork-a-repo/) the repository is on your GitHub account and [clone](https://help.github.com/articles/cloning-a-repository/) install it on your computer:

    ```bash
    git clone https://github.com/YOUR_USERNAME/Java-REST-API-Scrapper.git
    cd Java-REST-API-Scrapper/
    ```
    
2. Add the remote `upstream`:

    ```bash
    git remote add upstream https://github.com/ssukharev/Java-REST-API-Scrapper
    ```
    
3. Create new branch with your name:

   ```bash
   git checkout -b YOUR_BRANCH_NAME main
   ```

4. Add completed work or other changes

    - Be sure to follow the [design guidelines](#contributing-code).
    - If you need to update your fork, you can do so by doing a `rebase` with `upstream`:
      ```bash
      git fetch upstream
      git rebase upstream/main
      git push origin BRANCH_NAME -f
      ```

---
*Be sure to insert and use your API keys [here](src/main/resources/apiKeys.txt).* List of open API services used in this project:

- [NewsAPI](https://newsapi.org)

- [CurrentsAPI](https://currentsapi.services/en)

- [OpenWeatherMapAPI](https://openweathermap.org)

---
   
### <a name="contributing-code"></a>Design recommendations
- All commits **must** follow the [naming convention](https://www.conventionalcommits.org/en/v1.0.0/)
- Pull requests with commits that do not comply with this standard **will not** be accepted.
- Please make meaningful commit names or combine them (using `squash`) before opening the pool request.
  - Do not merge commits after someone has started checking your changes.
- Always use `rebase` for your commits to the current `main` branch. **Do not** use `merge main` in your branch.
- You are responsible for keeping your branch up to date. Your work **will not** be accepted unless it is migrated from the latest `main` branch.
- You can create a "draft" of the pull request in advance to get feedback on your work.
- Do not overload your work with unnecessary files that do not affect the understanding and performance of the completed task.
- To add comments to your work that you consider **important** for its completion, use README.md the files are in the root of their folder.
