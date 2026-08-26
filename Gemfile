source "https://rubygems.org"

gem "rexml", ">= 3.3.2"

# GHSA: Faraday NestedParamsEncoder DoS via unbounded nesting depth.
# Fixed in 1.10.6 (1.x branch) and 2.14.3 (2.x branch). Since the excon bump
# below, fastlane requires faraday ~> 2.7, so resolution lands on the patched
# 2.x line; the floor stays at 1.10.6 so either branch remains safe.
gem "faraday", ">= 1.10.6"

# CVE-2026-54171 (GHSA): Excon's RedirectFollower middleware did not strip
# additional sensitive headers when following a redirect and offered no way to
# supply a custom strip-list, risking leakage of Authorization/Cookie data to
# the redirect target. Fixed in 1.5.0.
#
# excon 1.x requires fastlane >= 2.237.0, which widened its constraint from
# "excon < 1.0.0" to "excon < 2.0.0". Both pins must move together.
gem "fastlane", ">= 2.237.0"
gem "excon", ">= 1.5.0"

plugins_path = File.join(File.dirname(__FILE__), 'fastlane', 'Pluginfile')
eval_gemfile(plugins_path) if File.exist?(plugins_path)