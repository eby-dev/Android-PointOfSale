source "https://rubygems.org"

gem "fastlane"
gem "rexml", ">= 3.3.2"

# GHSA: Faraday NestedParamsEncoder DoS via unbounded nesting depth.
# Fixed in 1.10.6 (1.x branch) and 2.14.3 (2.x branch). Fastlane pins to
# faraday ~> 1.0, so stay on 1.x and bump to the patched release.
gem "faraday", ">= 1.10.6"

plugins_path = File.join(File.dirname(__FILE__), 'fastlane', 'Pluginfile')
eval_gemfile(plugins_path) if File.exist?(plugins_path)